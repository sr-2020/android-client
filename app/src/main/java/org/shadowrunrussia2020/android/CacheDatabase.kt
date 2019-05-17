package org.shadowrunrussia2020.android

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import org.shadowrunrussia2020.android.models.billing.Balance
import org.shadowrunrussia2020.android.models.billing.Transaction

@Database(entities = [Balance::class, Transaction::class], version = 1)
@TypeConverters(Converters::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun billingDao(): BillingDao
}