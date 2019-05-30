package org.shadowrunrussia2020.android

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.shadowrunrussia2020.android.billing.BillingDao
import org.shadowrunrussia2020.android.billing.models.Balance
import org.shadowrunrussia2020.android.billing.models.Transaction

@Database(entities = [Balance::class, Transaction::class], version = 1)
@TypeConverters(Converters::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun billingDao(): BillingDao
}