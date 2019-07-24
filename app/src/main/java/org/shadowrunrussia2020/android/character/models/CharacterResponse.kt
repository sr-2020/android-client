package org.shadowrunrussia2020.android.character.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Character(@PrimaryKey val modelId: String, val spellsCasted: Int)

data class CharacterResponse(val workModel: Character)