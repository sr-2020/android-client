package org.shadowrunrussia2020.android.common.declaration.repository

import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.models.*

interface IBillingRepository {
    suspend fun refresh()
    fun getHistory(): LiveData<List<Transaction>>
    fun getRents(): LiveData<List<Rent>>
    fun getAccountOverview(): LiveData<AccountOverview>
    fun getScoringInfo(): LiveData<ScoringInfo>

    suspend fun transferMoney(transfer: Transfer)
}