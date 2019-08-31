package org.shadowrunrussia2020.android

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.shadowrunrussia2020.android.character.models.HistoryRecord
import org.shadowrunrussia2020.android.character.models.Spell
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
}