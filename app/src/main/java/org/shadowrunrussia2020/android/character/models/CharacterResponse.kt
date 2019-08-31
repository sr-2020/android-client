package org.shadowrunrussia2020.android.character.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Spell(
    val humanReadableName: String,
    val description: String,
    val eventType: String,
    val canTargetItem: Boolean,
    val canTargetLocation: Boolean,
    val canTargetSelf: Boolean,
    val canTargetSingleTarget: Boolean
) : Parcelable


@Parcelize
@Entity
data class HistoryRecord(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val title: String,
    val shortText: String,
    val longText: String
) : Parcelable

@Entity
data class Character(
    @PrimaryKey val modelId: String,
    val timestamp: Long,
    val healthState: String,
    val spellsCasted: Int,
    val spells: List<Spell>,
    var history: List<HistoryRecord>
)

data class CharacterResponse(var workModel: Character)