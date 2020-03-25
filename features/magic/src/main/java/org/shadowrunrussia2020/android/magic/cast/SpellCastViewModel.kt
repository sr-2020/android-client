package org.shadowrunrussia2020.android.magic.cast

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.utils.map
import org.shadowrunrussia2020.android.magic.MagicScreenDependency

class SpellCastViewModel(application: Application) : AndroidViewModel(application) {
    var power = 1

    private val dependency: MagicScreenDependency = ApplicationSingletonScope.DependencyProvider.provideDependency()

    private val mRepository = dependency.characterRepository

    val character get() = mRepository.getCharacter()

    var spellId = ""

    val spell get() = character.map { it?.spells?.find { it.id == spellId } }

    suspend fun refresh() = mRepository.refresh()

    suspend fun postEvent(eventType: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? =
        mRepository.sendEvent(Event(eventType, data))

    suspend fun castSpell(spellId: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? {
        data["id"] = spellId
        return postEvent("castSpell", data)
    }

}