package org.shadowrunrussia2020.android.model.qr

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class Type {
    UNKNOWN,
    REWRITABLE,
    PAYMENT_REQUEST_SIMPLE,
    PAYMENT_REQUEST_WITH_PRICE,
    SHOP_BILL,
    HEALTHY_BODY,
    WOUNDED_BODY,
    CLINICALLY_DEAD_BODY,
    ABSOLUTELY_DEAD_BODY,
    ASTRAL_BODY,
    empty,
    pill,
    implant,
    food,
    ability,
    artifact,
    event,
}

@Parcelize data class Data(
    val type: Type,
    val kind: Byte,
    val validUntil: Int,
    val payload: String): Parcelable

@Parcelize
data class FullQrData(
    val type: Type,
    val name: String,
    val description: String,
    val usesLeft: Int,
    val modelId: String): Parcelable

data class QrResponse(val workModel: FullQrData)