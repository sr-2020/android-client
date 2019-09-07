package org.shadowrunrussia2020.android

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.shadowrunrussia2020.android.billing.BillingDao
import org.shadowrunrussia2020.android.billing.models.AccountOverview
import org.shadowrunrussia2020.android.billing.models.Transaction
import org.shadowrunrussia2020.android.character.CharacterDao
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.character.models.HistoryRecord
import org.shadowrunrussia2020.android.positioning.Position
import org.shadowrunrussia2020.android.positioning.PositionsDao

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