package org.shadowrunrussia2020.android.model.qr

import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.Data
import org.shadowrunrussia2020.android.common.models.FullQrData
import org.shadowrunrussia2020.android.common.models.Type
import java.security.MessageDigest
import java.util.*

val Character.qrData: Data
    get() = Data(
        type = Type.HEALTHY_BODY,
        kind = 0,
        validUntil = (Date().time / 1000).toInt() + 3600,
        payload = this.modelId
    )

val Character.mentalQrData: Data
    get() = Data(
        type = Type.REWRITABLE,
        kind = 0,
        validUntil = (Date().time / 1000).toInt() + 3600,
        payload = this.mentalQrId.toString()
    )

private suspend fun downloadRewritableQrData(id: String): FullQrData {
    return withContext(Dispatchers.IO) {
        val d: ApplicationSingletonScope.Dependency =
            ApplicationSingletonScope.DependencyProvider.provideDependency()
        val qrService = d.qrRetrofit.create(QrWebService::class.java)
        val response = qrService.get(id).await()
        response.body()!!.workModel
    }
}

suspend fun retrieveQrData(data: Data): FullQrData {
    when (data.type) {
        Type.UNKNOWN -> TODO()
        Type.REWRITABLE ->
            return downloadRewritableQrData(data.payload)
        Type.PAYMENT_REQUEST_WITH_PRICE ->
            return FullQrData(data.type, "Запрос о переводе", "Запрос о переводе с указанием цены", 1, "");
        Type.PAYMENT_REQUEST_SIMPLE -> TODO()
        Type.SHOP_BILL ->
            return FullQrData(data.type, "Магазинный ценник", "Позволяет оплатить товар", 1, "");
        Type.HEALTHY_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. Здорово или легко ранено.", 1, data.payload);
        Type.WOUNDED_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. Тяжело ранено.", 1, data.payload);
        Type.CLINICALLY_DEAD_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. В состоянии клинической смерти.", 1, data.payload);
        Type.ABSOLUTELY_DEAD_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. Абсолютно мертво.", 1, data.payload);
        Type.ASTRAL_BODY ->
            return FullQrData(data.type, "Астральное тело", "", 1, data.payload);
        else -> TODO()
    }
}


class FormatException: Exception()
class ValidationException: Exception()
class ExpiredException: Exception()

internal fun signature(packedData: ByteArray, data: String): String {
    // TODO(aeremin): get salt from environment variable during build
    val salt = "do you like ponies?"
    val md = MessageDigest.getInstance("MD5")
    for (b in packedData) {
        // This is awful hack. "Golden" implementation (https://github.com/alice-larp/alice-larp-sdk/blob/master/packages/alice-qr-lib/qr.ts)
        // is kinda broken. It can add invalid (in UTF-8 sense) character in the string, which then gets converted to
        // the [239, 191, 189] triplet. See e.g.
        // 1) https://haacked.com/archive/2012/01/30/hazards-of-converting-binary-data-to-a-string.aspx/
        // 2) https://stackoverflow.com/questions/43918055/why-redis-returns-a-239-191-189-response-buffer
        // As we (at least for now) want to be compatible with "golden" implementation, we mimic such behaviour.
        if (b >= 0) {
            md.update(b)
        } else {
            md.update(239.toByte())
            md.update(191.toByte())
            md.update(189.toByte())
        }
    }
    //md.update(packedData)
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
