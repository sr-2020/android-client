package org.shadowrunrussia2020.android.common.utils

import org.shadowrunrussia2020.android.common.models.HealthState
import org.shadowrunrussia2020.android.common.models.ImplantGrade
import org.shadowrunrussia2020.android.common.models.ImplantSlot
import org.shadowrunrussia2020.android.common.models.QrType

fun russianSlotName(slot: ImplantSlot): String {
    return when (slot) {
        ImplantSlot.arm -> "Рука"
        ImplantSlot.head -> "Голова"
        ImplantSlot.body -> "Тело"
        ImplantSlot.rcc -> "RCC"
    }
}

fun russianGradeName(grade: ImplantGrade): String {
    return when (grade) {
        ImplantGrade.alpha -> "Альфа"
        ImplantGrade.beta -> "Бета"
        ImplantGrade.gamma -> "Гамма"
        ImplantGrade.delta -> "Дельта"
        ImplantGrade.bio -> "Биовар"
    }
}

fun russianHealthState(state: HealthState): String {
    return when (state) {
        HealthState.healthy -> "Здоров"
        HealthState.wounded -> "Тяжело ранен"
        HealthState.clinically_dead -> "КС"
        HealthState.biologically_dead -> "АС"
    }
}


fun russianQrType(type: QrType): String {
    return when (type) {
        QrType.UNKNOWN -> ""
        QrType.REWRITABLE -> ""
        QrType.PAYMENT_REQUEST_SIMPLE -> "Запрос о переводе"
        QrType.PAYMENT_REQUEST_WITH_PRICE -> "Запрос о переводе с ценой"
        QrType.SHOP_BILL -> "Магазинный ценник"
        QrType.HEALTHY_BODY -> "Здоровое мясное тело"
        QrType.WOUNDED_BODY -> "Мясное тело в тяжране"
        QrType.CLINICALLY_DEAD_BODY -> "Тело в КС"
        QrType.ABSOLUTELY_DEAD_BODY -> "Тело в АС"
        QrType.ASTRAL_BODY -> "Астральное тело"
        QrType.ROBOT_BODY -> "Дрон"
        QrType.empty -> "Пустышка"
        QrType.pill -> "Таблетка"
        QrType.implant -> "Имплант"
        QrType.food -> "Еда"
        QrType.ability -> "Способность"
        QrType.artifact -> "Артефакт"
        QrType.event -> "Событие"
        QrType.reagent -> "Реагент"
        QrType.locus -> "Локус этической группы"
        QrType.locus_charge -> "Заряд для локуса"
        QrType.box -> "Коробка"
    }
}