package org.shadowrunrussia2020.android.common

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.shadowrunrussia2020.android.common.models.ActiveAbility
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.PassiveAbility
import org.shadowrunrussia2020.android.common.models.Spell
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
}