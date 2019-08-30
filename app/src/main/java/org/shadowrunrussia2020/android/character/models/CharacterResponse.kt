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

@Entity
data class Character(
    @PrimaryKey val modelId: String,
    val healthState: String,
    val spellsCasted: Int,
    val spells: List<Spell>
)

data class CharacterResponse(val workModel: Character)