package org.shadowrunrussia2020.android.model.billing

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Rent
import org.shadowrunrussia2020.android.common.models.Transaction
import org.shadowrunrussia2020.android.common.models.Transfer

internal class BillingRepository(private val mService: BillingWebService, private val mBillingDao: BillingDao) :
    IBillingRepository {

    override suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val responseBalance = mService.balance().await()
            val responseHistory = mService.transfers().await()
            val rentsHistory = mService.rents().await()
            val accountInfo = responseBalance.body()
            val accountHistory = responseHistory.body()
            val rents = rentsHistory.body()
            if (accountInfo == null || accountHistory == null || rents == null) {
                Log.e("BillingRepository", "Invalid server response - body is empty")
            } else {
                mBillingDao.deleteTransactions()
                mBillingDao.insertTransactions(accountHistory.data.map { it })
                mBillingDao.deleteRents()
                mBillingDao.insertRents(rents.data.map { it })
                mBillingDao.setAccountOverview(accountInfo.data)
            }
        }
    }

    override fun getHistory(): LiveData<List<Transaction>> {
        return mBillingDao.history()
    }

    override fun getRents(): LiveData<List<Rent>> {
        return mBillingDao.rents()
    }

    override fun getAccountOverview(): LiveData<AccountOverview> {
        return mBillingDao.accountOverview()
    }

    override suspend fun transferMoney(transfer: Transfer) {
        withContext(Dispatchers.IO) {
            mService.transfer(transfer).await()
            refresh()
        }
    }
}