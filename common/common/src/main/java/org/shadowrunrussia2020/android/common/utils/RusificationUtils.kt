package org.shadowrunrussia2020.android.common.utils

import org.shadowrunrussia2020.android.common.models.ImplantGrade
import org.shadowrunrussia2020.android.common.models.ImplantSlot

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