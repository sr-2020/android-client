package org.shadowrunrussia2020.android.model.charter

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import retrofit2.Response

internal class CharacterRepository(private val mService: CharacterWebService, private val mDao: CharacterDao) :
    ICharacterRepository {

    override suspend fun refresh() {
        withContext(Dispatchers.IO) {
            saveToDao(mService.get().await())
        }
    }

    override fun getCharacter(): LiveData<Character> {
        return mDao.character()
    }

    override fun getHistory(): LiveData<List<HistoryRecord>> {
        return mDao.history()
    }

    override suspend fun sendEvent(event: Event): CharacterResponse? {
        return withContext(Dispatchers.IO) {
            saveToDao(mService.postEvent(event).await())
        }
    }

    override fun saveToDao(response: Response<CharacterResponse>): CharacterResponse? {
        val character = response.body()
        if (character == null) {
            Log.e("CharacterRepository", "Invalid server response - body is empty")
        } else {
            // We additionally store history separately for easier access and querying
            mDao.insertHistory(character.workModel.history)
            mDao.setCharacter(character.workModel)
        }
        return character
    }
}