package org.shadowrunrussia2020.android.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.ShadowrunRussia2020Application
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.character.models.CharacterResponse
import org.shadowrunrussia2020.android.character.models.Event
import org.shadowrunrussia2020.android.character.models.HistoryRecord

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = CharacterRepository(
        (application as ShadowrunRussia2020Application).retrofit.create(CharacterWebService::class.java),
        application.database.characterDao()
    )

    fun getCharacter(): LiveData<Character> {
        return mRepository.getCharacter()
    }

    fun getHistory(): LiveData<List<HistoryRecord>> {
        return mRepository.getHistory()
    }

    suspend fun refresh() {
        return mRepository.refresh()
    }

    suspend fun postEvent(eventType: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? {
        return mRepository.sendEvent(Event(eventType, data))
    }
}