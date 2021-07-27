package org.shadowrunrussia2020.android.common.utils

import org.shadowrunrussia2020.android.common.models.*
import kotlin.math.ceil

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
        QrType.PAYMENT_REQUEST_WITH_PRICE -> "Запрос о переводе с ценой"
        QrType.SHOP_BILL -> "Магазинный ценник"
        QrType.HEALTHY_BODY -> "Здоровое мясное тело"
        QrType.WOUNDED_BODY -> "Мясное тело в тяжране"
        QrType.CLINICALLY_DEAD_BODY -> "Тело в КС"
        QrType.ABSOLUTELY_DEAD_BODY -> "Тело в АС"
        QrType.ASTRAL_BODY -> "Астральное тело"
        QrType.ROBOT_BODY -> "Дрон"
        QrType.ECTOPLASM_BODY -> "Дух"
        QrType.VR_BODY -> "Аватар VR"
        QrType.empty -> "Пустышка"
        QrType.pill -> "Препарат"
        QrType.implant -> "Имплант"
        QrType.food -> "Еда"
        QrType.ability -> "Способность"
        QrType.feature_to_buy -> "Способность для покупки"
        QrType.artifact -> "Артефакт"
        QrType.event -> "Событие"
        QrType.reagent -> "Реагент"
        QrType.locus -> "Локус этической группы"
        QrType.locus_charge -> "Заряд для локуса"
        QrType.box -> "Коробка"
        QrType.body_storage -> "Телохранилище"
        QrType.spirit -> "Дух"
        QrType.spirit_jar -> "Духохранилище"
        QrType.drone -> "Дрон"
        QrType.drone_mod -> "Мод для дрона"
        QrType.sprite -> "Спрайт"
        QrType.cyberdeck -> "Кибердека"
        QrType.cyberdeck_mod -> "Мод для кибердеки"
        QrType.software -> "Софт"
        QrType.foundation_node -> "Нода основания"
        QrType.reanimate_capsule -> "Воскрешательная капсула"
        QrType.ai_symbol -> "Символ ИИ"
        QrType.focus -> "Магический фокус"
        QrType.focus_on_cooldown -> "Магический фокус (кулдаун)"
        QrType.repair_kit -> "Ремкомплект"
        QrType.armour -> "Броня"
        QrType.weapon -> "Оружие"
    }
}

fun russianSpellSphere(sphere: SpellSphere): String {
    return when (sphere) {
        SpellSphere.healing -> "Лечение"
        SpellSphere.fighting -> "Боевая"
        SpellSphere.protection -> "Защита"
        SpellSphere.astral -> "Анализ астрала"
        SpellSphere.aura -> "Анализ ауры"
        SpellSphere.stats -> "Влияние на характеристики"
    }
}

fun timeBeforeInMinutes(nowMs: Long, beforeMs: Long): String {
    val delta = beforeMs - nowMs
    val minutes = ceil(delta / (60.0 * 1000)).toInt()
    return "$minutes минут"
}