package org.shadowrunrussia2020.android.common

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.shadowrunrussia2020.android.common.models.*
import java.util.*


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun jsonToSpells(value: String): List<Spell> {
        return Gson().fromJson(value, object : TypeToken<List<Spell>>() {}.type)
    }

    @TypeConverter
    fun spellsToJson(value: List<Spell>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToHistory(value: String): List<HistoryRecord> {
        return Gson().fromJson(value, object : TypeToken<List<HistoryRecord>>() {}.type)
    }

    @TypeConverter
    fun historyToJson(value: List<HistoryRecord>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToActiveAbilities(value: String): List<ActiveAbility> {
        return Gson().fromJson(value, object : TypeToken<List<ActiveAbility>>() {}.type)
    }

    @TypeConverter
    fun activeAbilitiesToJson(value: List<ActiveAbility>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToPassiveAbilities(value: String): List<PassiveAbility> {
        return Gson().fromJson(value, object : TypeToken<List<PassiveAbility>>() {}.type)
    }

    @TypeConverter
    fun passiveAbilitiesToJson(value: List<PassiveAbility>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToEthicTrigger(value: String): List<EthicTrigger> {
        return Gson().fromJson(value, object : TypeToken<List<EthicTrigger>>() {}.type)
    }

    @TypeConverter
    fun ethicTriggerToJson(value: List<EthicTrigger>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToEthicState(value: String): List<EthicState> {
        return Gson().fromJson(value, object : TypeToken<List<EthicState>>() {}.type)
    }

    @TypeConverter
    fun ethicStateToJson(value: List<EthicState>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToImplants(value: String): List<Implant> {
        return Gson().fromJson(value, object : TypeToken<List<Implant>>() {}.type)
    }

    @TypeConverter
    fun implantsToJson(value: List<Implant>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToModifiers(value: String): List<Modifier> {
        return Gson().fromJson(value, object : TypeToken<List<Modifier>>() {}.type)
    }

    @TypeConverter
    fun modifiersToJson(value: List<Modifier>): String {
        return Gson().toJson(value)
    }
}