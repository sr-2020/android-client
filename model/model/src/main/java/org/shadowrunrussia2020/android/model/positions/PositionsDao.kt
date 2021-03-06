package org.shadowrunrussia2020.android.model.positions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.shadowrunrussia2020.android.common.models.Position

@Dao
internal interface PositionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPositions(transactions: List<Position>)

    @Query("SELECT * FROM `Position` ORDER BY id DESC")
    fun positions(): LiveData<List<Position>>
}


