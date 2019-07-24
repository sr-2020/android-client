package org.shadowrunrussia2020.android.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.ShadowrunRussia2020Application
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.character.models.Event

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = CharacterRepository(
        (application as ShadowrunRussia2020Application).getRetrofit().create(CharacterWebService::class.java),
        application.getDatabase().characterDao()
    )

    fun getCharacter(): LiveData<Character> {
        return mRepository.getCharacter()
    }

    suspend fun refresh() {
        return mRepository.refresh()
    }

    suspend fun postEvent(eventType: String) {
        return mRepository.sendEvent(Event(eventType))
    }
}