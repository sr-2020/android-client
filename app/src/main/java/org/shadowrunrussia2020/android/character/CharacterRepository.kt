package org.shadowrunrussia2020.android.character

import android.util.Log
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.character.models.CharacterResponse
import org.shadowrunrussia2020.android.character.models.Event
import retrofit2.Response

class CharacterRepository(private val mService: CharacterWebService, private val mDao: CharacterDao) {
    suspend fun refresh() {
        saveToDao(mService.get().await())
    }

    fun getCharacter(): LiveData<Character> {
        return mDao.character()
    }

    suspend fun sendEvent(event: Event) {
        saveToDao(mService.postEvent(event).await())
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