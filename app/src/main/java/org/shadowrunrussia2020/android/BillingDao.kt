package org.shadowrunrussia2020.android

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import org.shadowrunrussia2020.android.models.billing.Balance
import org.shadowrunrussia2020.android.models.billing.Transaction

@Dao
interface BillingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setBalance(balance: Balance)

    @Query("SELECT * FROM `Transaction` ORDER BY created_at DESC")
    fun history(): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Balance`")
    fun balance(): LiveData<Balance>
}