package org.shadowrunrussia2020.android.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.shadowrunrussia2020.android.common.Converters
import org.shadowrunrussia2020.android.common.models.*
import org.shadowrunrussia2020.android.model.billing.BillingDao
import org.shadowrunrussia2020.android.model.charter.CharacterDao
import org.shadowrunrussia2020.android.model.positions.PositionsDao

@Database(
    entities = [
        AccountOverview::class,
        Transaction::class,
        Character::class,
        HistoryRecord::class,
        Position::class
    ],
    version = 26
)
@TypeConverters(Converters::class)
internal abstract class CacheDatabase : RoomDatabase() {
    abstract fun billingDao(): BillingDao
    abstract fun characterDao(): CharacterDao
    abstract fun positionsDao(): PositionsDao

    companion object {
        fun build(context: Context) =
            Room.databaseBuilder(context, CacheDatabase::class.java, "cache-db").fallbackToDestructiveMigration().build()
    }
}