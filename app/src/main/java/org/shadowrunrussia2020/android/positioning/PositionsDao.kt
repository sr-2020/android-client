package org.shadowrunrussia2020.android.positioning

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PositionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPositions(transactions: List<Position>)

    @Query("SELECT * FROM `Position` ORDER BY username DESC")
    fun positions(): LiveData<List<Position>>
}


