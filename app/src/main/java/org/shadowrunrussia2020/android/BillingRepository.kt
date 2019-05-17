package org.shadowrunrussia2020.android

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import org.jetbrains.anko.doAsync
import org.shadowrunrussia2020.android.models.billing.AccountInfo
import org.shadowrunrussia2020.android.models.billing.Balance
import org.shadowrunrussia2020.android.models.billing.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BillingRepository(private val mService: BillingWebService, private val mBillingDao: BillingDao) {
    fun refresh() {
        mService.accountInfo().enqueue(object : Callback<AccountInfo> {
            override fun onResponse(call: Call<AccountInfo>, response: Response<AccountInfo>) {
                Log.i("BillingRepository", "Http request succeeded, response = " + response.body())
                val accountInfo = response.body()
                if (accountInfo == null) {
                    Log.e("BillingRepository", "Invalid server response - body is empty")
                    return
                }
                doAsync {
                    mBillingDao.insertTransactions(accountInfo.history)
                    mBillingDao.setBalance(Balance(0, accountInfo.balance))
                }
            }

            override fun onFailure(call: Call<AccountInfo>, t: Throwable) {
                Log.e("BillingRepository", "Http request failed: $t")
            }
        })
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
}