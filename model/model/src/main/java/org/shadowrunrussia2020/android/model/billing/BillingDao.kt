package org.shadowrunrussia2020.android.model.billing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Rent
import org.shadowrunrussia2020.android.common.models.Transaction

@Dao
internal interface BillingDao {
    @Query("DELETE FROM `Transaction`")
    fun deleteTransactions()

    @Query("DELETE FROM Rent")
    fun deleteRents()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRents(rents: List<Rent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAccountOverview(overview: AccountOverview)

    @Query("SELECT * FROM `Transaction` ORDER BY operationTime DESC")
    fun history(): LiveData<List<Transaction>>

    @Query("SELECT * FROM Rent ORDER BY dateCreated DESC")
    fun rents(): LiveData<List<Rent>>

    @Query("SELECT * FROM `AccountOverview`")
    fun accountOverview(): LiveData<AccountOverview>
}