package org.shadowrunrussia2020.android.model.billing

import androidx.lifecycle.LiveData
import android.util.Log
import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Transaction
import org.shadowrunrussia2020.android.common.models.Transfer
import org.shadowrunrussia2020.android.common.models.Empty
import retrofit2.Response

class BillingRepository(private val mService: BillingWebService, private val mBillingDao: BillingDao) :
    IBillingRepository {
    override suspend fun refresh() {
        val response = mService.accountInfo().await()
        val accountInfo = response.body()
        if (accountInfo == null) {
            Log.e("BillingRepository", "Invalid server response - body is empty")
        } else {
            mBillingDao.insertTransactions(accountInfo.history.map {
                    if (it.sin_from == accountInfo.sin) {
                        it.amount = -it.amount
                    }
                it
            })
            mBillingDao.setAccountOverview(
                AccountOverview(id = 0, sin = accountInfo.sin, balance = accountInfo.balance)
            )
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