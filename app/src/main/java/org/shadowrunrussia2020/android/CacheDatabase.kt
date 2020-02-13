package org.shadowrunrussia2020.android

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.shadowrunrussia2020.android.common.declaration.BillingDao
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Transaction
import org.shadowrunrussia2020.android.common.declaration.CharacterDao
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.common.declaration.PositionsDao

@Database(
    entities = [
        AccountOverview::class,
        Transaction::class,
        Character::class,
        HistoryRecord::class,
        Position::class
    ],
    version = 10
)
@TypeConverters(Converters::class)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun billingDao(): BillingDao
    abstract fun characterDao(): CharacterDao
    abstract fun positionsDao(): PositionsDao
}