package org.shadowrunrussia2020.android.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.HistoryRecord

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = ApplicationSingletonScope.ComponentProvider.components.charterRepository

    fun getCharacter(): LiveData<Character> {
        return mRepository.getCharacter()
    }

    fun getHistory(): LiveData<List<HistoryRecord>> {
        return mRepository.getHistory()
    }

    suspend fun refresh() {
        return mRepository.refresh()
    }

    suspend fun castSpell(spellId: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? {
        data["id"] = spellId
        return postEvent("castSpell", data)
    }

    suspend fun postEvent(eventType: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? {
        return mRepository.sendEvent(Event(eventType, data))
    }
}