package org.shadowrunrussia2020.android.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class QrType {
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
    ROBOT_BODY,
    ECTOPLASM_BODY,
    empty,
    pill,
    implant,
    food,
    ability,
    feature_to_buy,
    artifact,
    event,
    reagent,
    locus,
    locus_charge,
    box,
    body_storage,
    spirit,
    drone,
    drone_mod,
    sprite,
    cyberdeck,
    cyberdeck_mod,
    software,
    foundation_node,
    reanimate_capsule,
    ai_symbol,
    focus,
    focus_on_cooldown,
    repair_kit,
}

@Parcelize data class SimpleQrData(
    val type: QrType,
    val kind: Byte,
    val validUntil: Int,
    val payload: String): Parcelable

@Parcelize data class InternalQrData(
    val sphere: SpellSphere? = null,
    val amount: Int? = null
): Parcelable

@Parcelize
data class FullQrData(
    val type: QrType,
    val name: String,
    val description: String,
    val usesLeft: Int,
    val modelId: String,
    val data: InternalQrData = InternalQrData()): Parcelable

data class QrResponse(val workModel: FullQrData)