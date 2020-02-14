package org.shadowrunrussia2020.android.common.declaration.repository

import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import retrofit2.Response

interface ICharacterRepository {
    suspend fun refresh()
    fun getCharacter(): LiveData<Character>
    fun getHistory(): LiveData<List<HistoryRecord>>

    suspend fun sendEvent(event: Event): CharacterResponse?
    fun saveToDao(response: Response<CharacterResponse>): CharacterResponse?
}