package org.shadowrunrussia2020.android.character.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Spell(
    val eventType: String,
    val description: String,
    val canTargetItem: Boolean,
    val canTargetLocation: Boolean,
    val canTargetSelf: Boolean,
    val canTargetSingleTarget: Boolean
)

@Entity
data class Character(@PrimaryKey val modelId: String, val spellsCasted: Int, val spells: List<Spell>)

data class CharacterResponse(val workModel: Character)