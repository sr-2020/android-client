package org.shadowrunrussia2020.android

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.models.billing.*
import retrofit2.Response

class BillingRepository(private val mService: BillingWebService, private val mBillingDao: BillingDao) {
    suspend fun refresh() {
        val response = mService.accountInfo().await()
        val accountInfo = response.body()
        if (accountInfo == null) {
            Log.e("BillingRepository", "Invalid server response - body is empty")
        } else {
            mBillingDao.insertTransactions(accountInfo.history)
            mBillingDao.setBalance(Balance(0, accountInfo.balance))
        }
    }

    fun getHistory(): LiveData<List<Transaction>> {
        return mBillingDao.history()
    }

    private val mBalanceData = MediatorLiveData<Int>()

    init {
        mBalanceData.addSource(mBillingDao.balance())
        { item: Balance? -> mBalanceData.value = item?.balance ?: 0 }
    }

    fun getBalance(): LiveData<Int> {
        return mBalanceData
    }

    suspend fun transferMoney(transfer: Transfer): Response<Empty> {
        val result = mService.transfer(transfer).await()
        refresh()
        return result
    }
}