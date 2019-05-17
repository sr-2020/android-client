package org.shadowrunrussia2020.android.qr

import com.google.common.io.BaseEncoding
import okio.Buffer
import java.security.MessageDigest
import java.util.*

enum class Type {
    UNKNOWN,
    GRIMOIRE,
    ALCHEMY_REAGENT,
    CRAFTED_ITEM,
    ENCHANTED_ITEM,
    ALCHEMY_POTION,
    PAYMENT_REQUEST_SIMPLE,
    PAYMENT_REQUEST_WITH_PRICE,
    SHOP_BILL,
    DIGITAL_SIGNATURE,
    PASSPORT,
}

data class Data(val type: Type, val kind: Byte, val validUntil: Int, val payload: String)

class FormatException(): Exception()
class ValidationException(): Exception()
class ExpiredException(): Exception()

internal fun signature(packedData: ByteArray, data: String): String {
    // TODO(aeremin): get salt from environment variable during build
    val salt = "do you like ponies?"
    val md = MessageDigest.getInstance("MD5")
    md.update(packedData)
    md.update(data.toByteArray())
    md.update(salt.toByteArray())
    val md5hash = md.digest()
    return BaseEncoding.base16().lowerCase().encode(md5hash.sliceArray(IntRange(0, 1)))
}

fun encode(data: Data): String {
    var packedData = Buffer()
        .writeByte(data.type.ordinal)
        .writeByte(data.kind.toInt())
        .writeIntLe(data.validUntil)
        .readByteArray()

    return signature(packedData, data.payload) + BaseEncoding.base64().encode(packedData) + data.payload
}

internal fun parseHeader(content: String): ByteArray {
    try {
        return BaseEncoding.base64().decode(content.slice(IntRange(4, 11)))
    } catch (e: java.lang.Exception) {
        throw FormatException()
    }
}

fun decode(content: String): Data {
    if (content.length < 12)
        throw FormatException()

    val buf = Buffer().write(parseHeader(content))
    val bufCopy = buf.clone()
    val type = buf.readByte()
    val kind = buf.readByte()
    val validUntil = buf.readIntLe()
    val expectedSignature = signature(bufCopy.readByteArray(), content.slice(IntRange(12, content.length - 1)))
    if (content.slice(IntRange(0, 3)) != expectedSignature)
        throw ValidationException()

    if (validUntil < Date().time / 1000)
        throw ExpiredException()

    val payload = content.slice(IntRange(12, content.length - 1))
    return Data(Type.values()[type.toInt()], kind, validUntil, payload)
}
