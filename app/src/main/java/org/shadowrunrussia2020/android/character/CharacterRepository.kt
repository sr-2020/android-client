package org.shadowrunrussia2020.android.character

import android.util.Log
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.character.models.CharacterResponse
import org.shadowrunrussia2020.android.character.models.Event
import retrofit2.Response

class CharacterRepository(private val mService: CharacterWebService, private val mDao: CharacterDao) {
    suspend fun refresh() {
        // TODO: Get id from somewhere?
        saveToDao(mService.get("8").await())
    }

    fun getCharacter(): LiveData<Character> {
        return mDao.character()
    }

    suspend fun sendEvent(event: Event) {
        // TODO: Get id from somewhere?
        saveToDao(mService.postEvent("8", event).await())
    }

    private fun saveToDao(response: Response<CharacterResponse>) {
        val character = response.body()
        if (character == null) {
            Log.e("CharacterRepository", "Invalid server response - body is empty")
        } else {
            mDao.setCharacter(character.workModel)
        }
    }
}