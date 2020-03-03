package org.shadowrunrussia2020.android.model.billing

import android.util.Log
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Empty
import org.shadowrunrussia2020.android.common.models.Transaction
import org.shadowrunrussia2020.android.common.models.Transfer
import retrofit2.Response

internal class BillingRepository(private val mService: BillingWebService, private val mBillingDao: BillingDao) :
    IBillingRepository {
    override suspend fun refresh() {
        val responseBalance = mService.balance().await()
        val responseHistory = mService.transfers().await()
        val accountInfo = responseBalance.body()
        val accountHistory = responseHistory.body()
        if (accountInfo == null || accountHistory == null) {
            Log.e("BillingRepository", "Invalid server response - body is empty")
        } else {
            mBillingDao.deleteTransactions()
            mBillingDao.insertTransactions(accountHistory.data.map { it })
            mBillingDao.setAccountOverview(accountInfo.data)
        }
    }

    override fun getHistory(): LiveData<List<Transaction>> {
        return mBillingDao.history()
    }

    override fun getAccountOverview(): LiveData<AccountOverview> {
        return mBillingDao.accountOverview()
    }
    override suspend fun transferMoney(transfer: Transfer): Response<Empty> {
        val result = mService.transfer(transfer).await()
        refresh()
        return result
    }
}