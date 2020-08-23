package org.shadowrunrussia2020.android.model.qr

import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.*
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

fun getQrType(character: Character): QrType {
    return when (character.currentBody) {
        BodyType.astral -> QrType.ASTRAL_BODY
        BodyType.drone -> QrType.ROBOT_BODY
        BodyType.physical -> when (character.healthState) {
            HealthState.healthy -> QrType.HEALTHY_BODY
            HealthState.wounded -> QrType.WOUNDED_BODY
            HealthState.clinically_dead -> QrType.CLINICALLY_DEAD_BODY
            HealthState.biologically_dead -> QrType.ABSOLUTELY_DEAD_BODY
        }
    }
}

val Character.qrData: SimpleQrData
    get() = SimpleQrData(
            type = getQrType(this),
            kind = 0,
            validUntil = (TimeUnit.MILLISECONDS.toSeconds(this.timestamp) + TimeUnit.HOURS.toSeconds(1)).toInt(),
            payload = this.modelId
        )

val Character.mentalQrData: SimpleQrData
    get() = SimpleQrData(
        type = QrType.REWRITABLE,
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

suspend fun retrieveQrData(data: SimpleQrData): FullQrData {
    when (data.type) {
        QrType.UNKNOWN -> TODO()
        QrType.REWRITABLE ->
            return downloadRewritableQrData(data.payload)
        QrType.PAYMENT_REQUEST_WITH_PRICE ->
            return FullQrData(data.type, "Запрос о переводе", "Запрос о переводе с указанием цены", 1, "");
        QrType.PAYMENT_REQUEST_SIMPLE -> TODO()
        QrType.SHOP_BILL ->
            return FullQrData(data.type, "Магазинный ценник", "Позволяет оплатить товар", 1, data.payload);
        QrType.HEALTHY_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. Здорово или легко ранено.", 1, data.payload);
        QrType.WOUNDED_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. Тяжело ранено.", 1, data.payload);
        QrType.CLINICALLY_DEAD_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. В состоянии клинической смерти.", 1, data.payload);
        QrType.ABSOLUTELY_DEAD_BODY ->
            return FullQrData(data.type, "Мясное тело", "Мясное тело. Абсолютно мертво.", 1, data.payload);
        QrType.ASTRAL_BODY ->
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
    md.update(packedData)
    md.update(data.toByteArray())
    md.update(salt.toByteArray())
    val md5hash = md.digest()
    return BaseEncoding.base16().lowerCase().encode(md5hash.sliceArray(IntRange(0, 1)))
}

fun encode(data: SimpleQrData): String {
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

fun decode(content: String): SimpleQrData {
    val d = decodeNoTimeCheck(content)

    if (d.validUntil < Date().time / 1000)
        throw ExpiredException()

    return d
}

fun decodeNoTimeCheck(content: String): SimpleQrData {
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
    val payload = content.slice(IntRange(12, content.length - 1))
    return SimpleQrData(QrType.values()[type.toInt()], kind, validUntil, payload)
}