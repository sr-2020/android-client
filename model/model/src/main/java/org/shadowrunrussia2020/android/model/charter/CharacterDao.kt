package org.shadowrunrussia2020.android.model.charter

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.HistoryRecord

@Dao
internal interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setCharacter(character: Character)

    @Query("SELECT * FROM `Character`")
    fun character(): LiveData<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(transactions: List<HistoryRecord>)

    @Query("SELECT * FROM `HistoryRecord` ORDER BY timestamp DESC")
    fun history(): LiveData<List<HistoryRecord>>
}


