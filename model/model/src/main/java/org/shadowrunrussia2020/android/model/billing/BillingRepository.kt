package org.shadowrunrussia2020.android.model.billing

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.models.*

internal class BillingRepository(private val mService: BillingWebService, private val mBillingDao: BillingDao) :
    IBillingRepository {

    override suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val accountInfo = mService.balance().await().body()
            val accountHistory = mService.transfers().await().body()
            val rents = mService.rents().await().body()
            val scoringData = mService.scoring().await().body()
            if (accountInfo == null || accountHistory == null || rents == null || scoringData == null) {
                Log.e("BillingRepository", "Invalid server response - body is empty")
            } else {
                mBillingDao.deleteTransactions()
                mBillingDao.insertTransactions(accountHistory.data.map { it })
                mBillingDao.deleteRents()
                mBillingDao.insertRents(rents.data.rentas.map { it })

                accountInfo.data.sumRents = rents.data.sum
                mBillingDao.setAccountOverview(accountInfo.data)

                mBillingDao.setScoringInfo(scoringData.data)
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

    override fun getScoringInfo(): LiveData<ScoringInfo> {
        return mBillingDao.scoringInfo()
    }

    override suspend fun transferMoney(transfer: Transfer) {
        withContext(Dispatchers.IO) {
            mService.transfer(transfer).await()
            refresh()
        }
    }
}