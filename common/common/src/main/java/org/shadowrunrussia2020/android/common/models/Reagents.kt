package org.shadowrunrussia2020.android.common.models

data class ReagentContent(
    var virgo: Int = 0,
    var taurus: Int = 0,
    var aries: Int = 0,
    var cancer: Int = 0,
    var gemini: Int = 0,
    var capricorn: Int = 0,
    var ophiuchus: Int = 0,
    var pisces: Int = 0,
    var sagittarius: Int = 0,
    var leo: Int = 0,
    var libra: Int = 0,
    var aquarius: Int = 0,
    var scorpio: Int = 0
) {
    operator fun plus(increment: ReagentContent): ReagentContent {
        return ReagentContent(
            virgo + increment.virgo, taurus + increment.taurus,
            aries + increment.aries, cancer + increment.cancer,
            gemini + increment.gemini, capricorn + increment.capricorn,
            ophiuchus + increment.ophiuchus, pisces + increment.pisces,
            sagittarius + increment.sagittarius, leo + increment.leo,
            libra + increment.libra, aquarius + increment.aquarius,
            scorpio + increment.scorpio
        )
    }

    fun stringify(): String {
        val parts = mutableListOf<String>()
        if (virgo != 0) parts.add("Дева: ${virgo}")
        if (taurus != 0) parts.add("Телец: ${taurus}")
        if (aries != 0) parts.add("Овен: ${aries}")
        if (cancer != 0) parts.add("Рак: ${cancer}")
        if (gemini != 0) parts.add("Близнецы: ${gemini}")
        if (capricorn != 0) parts.add("Козерог: ${capricorn}")
        if (ophiuchus != 0) parts.add("Змееносец: ${ophiuchus}")
        if (pisces != 0) parts.add("Рыбы: ${pisces}")
        if (sagittarius != 0) parts.add("Стрелец: ${sagittarius}")
        if (leo != 0) parts.add("Лев: ${leo}")
        if (libra != 0) parts.add("Весы: ${libra}")
        if (aquarius != 0) parts.add("Водолей: ${aquarius}")
        if (scorpio != 0) parts.add("Скорпион: ${scorpio}")
        return parts.joinToString(", ")
    }
}

data class Reagent(val id: String, val name: String, val content: ReagentContent)