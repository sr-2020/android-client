package org.shadowrunrussia2020.android.common.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

enum class SpellSphere { healing, fighting, protection, astral, aura, stats }

@Parcelize
data class Spell(
    val id: String,
    val humanReadableName: String,
    val description: String,
    val hasTarget: Boolean,
    val sphere: SpellSphere
) : Parcelable

enum class TargetType { show, scan }

@Parcelize
data class TargetSignature(
    val name: String,
    val allowedTypes: List<QrType>,
    val field: String
) : Parcelable

@Parcelize
data class ActiveAbility(
    val id: String,
    val humanReadableName: String,
    val description: String,
    val target: TargetType,
    val targetsSignature: List<TargetSignature>,
    val validUntil: Long?,
    val cooldownMinutes: Float,
    val cooldownUntil: Long
) : Parcelable

@Parcelize
data class PassiveAbility(
    val id: String,
    val humanReadableName: String,
    val description: String,
    val validUntil: Long?
) : Parcelable  {
    companion object :DiffUtil.ItemCallback<PassiveAbility>() {
        override fun areItemsTheSame(oldItem: PassiveAbility, newItem: PassiveAbility): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PassiveAbility, newItem: PassiveAbility): Boolean = oldItem == newItem
    }
}

@Parcelize
data class EthicState(
    val scale: String,
    val value: Int,
    val description: String
) : Parcelable

@Parcelize
data class EthicTrigger(
    val id: String,
    val kind: String,
    val description: String
) : Parcelable

enum class ImplantSlot { body, arm, head, rcc }
enum class ImplantGrade { alpha, beta, gamma, delta, bio }
enum class HealthState { healthy, wounded, clinically_dead, biologically_dead }
enum class BodyType { physical, astral, drone, ectoplasm }

@Parcelize
data class Implant(
    val id: String,
    val name: String,
    val description: String,
    val slot: ImplantSlot,
    val grade: ImplantGrade
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

@Parcelize
data class SpellTrace(
    val timestamp: Long,
    val spellName: String,
    val casterAura: String,
    val metarace: String,
    val power: Int,
    val magicFeedback: Int
) : Parcelable

@Parcelize
data class Modifier(
    val mID: String,
    val enabled: Boolean
) : Parcelable

@Parcelize
data class Timer(
    val description: String,
    val miliseconds: Long
) : Parcelable

@Parcelize
data class AnalyzedBody(
    val healthState: HealthState,
    val essence: Int,
    val implants: List<Implant>
) : Parcelable

data class Ethic(
    val state: List<EthicState>,
    val trigger: List<EthicTrigger>,
    val lockedUntil: Long
)

data class MagicStats(
    val maxPowerBonus: Int
)

data class Hacking(
    val fading: Int
)

data class Karma(
    val available: Float,
    val spent: Float,
    val cycleLimit: Float
)

data class Screens(
    val billing: Boolean,
    val spellbook: Boolean,
    val activeAbilities: Boolean,
    val passiveAbilities: Boolean,
    val karma: Boolean,
    val implants: Boolean,
    val autodoc: Boolean,
    val autodocWoundHeal: Boolean,
    val autodocImplantInstall: Boolean,
    val autodocImplantRemoval: Boolean,
    val ethics: Boolean,
    val location: Boolean,
    val wound: Boolean,
    val scanQr: Boolean,
    val scoring: Boolean
)

@Entity
data class Character(
    @PrimaryKey val modelId: String,
    val timestamp: Long,
    val maxHp: Int,
    val healthState: HealthState,
    val currentBody: BodyType,
    val metarace: String,
    val body: Int,
    val intelligence: Int,
    val charisma: Int,
    val essence: Int,
    val mentalQrId: Int,
    val magic: Int,
    val depth: Int,
    val resonance: Int,
    val strength: Int,
    val matrixHp: Int,
    val maxTimeInVr: Int,
    val spells: List<Spell>,
    val activeAbilities: List<ActiveAbility>,
    val passiveAbilities: List<PassiveAbility>,
    val implants: List<Implant>,
    val history: List<HistoryRecord>,
    val modifiers: List<Modifier>,
    val timers: List<Timer>,
    val analyzedBody: AnalyzedBody?,
    @Embedded
    val ethic: Ethic,
    @Embedded
    val magicStats: MagicStats,
    @Embedded
    val hacking: Hacking,
    @Embedded
    val karma: Karma,
    @Embedded(prefix = "screens")
    val screens: Screens
)

data class CharacterResponse(val workModel: Character, val tableResponse: List<SpellTrace>?)

@Parcelize
data class Feature (
    val id: String,
    val humanReadableName: String,
    val description: String,
    val prerequisites: List<String>,
    val karmaCost: Float
): Parcelable