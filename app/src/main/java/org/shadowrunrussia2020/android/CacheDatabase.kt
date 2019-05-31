package org.shadowrunrussia2020.android

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.shadowrunrussia2020.android.billing.BillingDao
import org.shadowrunrussia2020.android.billing.models.AccountOverview
import org.shadowrunrussia2020.android.billing.models.Transaction

@Database(entities = [AccountOverview::class, Transaction::class], version = 2)
@TypeConverters(Converters::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun billingDao(): BillingDao
}