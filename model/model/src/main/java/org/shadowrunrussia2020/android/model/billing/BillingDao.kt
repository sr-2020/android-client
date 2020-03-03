package org.shadowrunrussia2020.android.model.billing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Transaction

@Dao
internal interface BillingDao {
    @Query("DELETE FROM `Transaction`")
    fun deleteTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAccountOverview(overview: AccountOverview)

    @Query("SELECT * FROM `Transaction` ORDER BY operationTime DESC")
    fun history(): LiveData<List<Transaction>>

    @Query("SELECT * FROM `AccountOverview`")
    fun accountOverview(): LiveData<AccountOverview>
}