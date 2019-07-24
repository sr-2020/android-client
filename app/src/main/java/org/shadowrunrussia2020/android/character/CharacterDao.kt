package org.shadowrunrussia2020.android.character

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.shadowrunrussia2020.android.character.models.Character

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setCharacter(character: Character)

    @Query("SELECT * FROM `Character`")
    fun character(): LiveData<Character>
}
