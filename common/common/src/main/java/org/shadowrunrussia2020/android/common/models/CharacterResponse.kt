package org.shadowrunrussia2020.android.common.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Spell(
    val id: String,
    val humanReadableName: String,
    val description: String,
    val eventType: String
) : Parcelable

@Parcelize
data class ActiveAbility(
    val humanReadableName: String,
    val description: String,
    val eventType: String,
    val canTargetSelf: Boolean,
    val canTargetSingleTarget: Boolean
) : Parcelable

@Parcelize
data class PassiveAbility(
    val id: String,
    val name: String,
    val description: String
) : Parcelable  {
    companion object :DiffUtil.ItemCallback<PassiveAbility>() {
        override fun areItemsTheSame(oldItem: PassiveAbility, newItem: PassiveAbility): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PassiveAbility, newItem: PassiveAbility): Boolean = oldItem == newItem
    }
}

@Parcelize
@Entity
data class HistoryRecord(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val title: String,
    val shortText: String,
    val longText: String
) : Parcelable

@Parcelize
data class SpellTrace(
    val timestamp: Long,
    val spellName: String,
    val casterAura: String,
    val power: Int,
    val magicFeedback: Int
) : Parcelable

@Entity
data class Character(
    @PrimaryKey val modelId: String,
    val timestamp: Long,
    val healthState: String,
    val maxHp: Int,
    val magic: Int,
    val magicPowerBonus: Int,
    val spellsCasted: Int,
    val spells: List<Spell>,
    val activeAbilities: List<ActiveAbility>,
    val passiveAbilities: List<PassiveAbility>,
    val history: List<HistoryRecord>
)

data class CharacterResponse(val workModel: Character, val tableResponse: List<SpellTrace>?)