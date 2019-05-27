package org.shadowrunrussia2020.android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import org.shadowrunrussia2020.android.models.billing.Empty
import org.shadowrunrussia2020.android.models.billing.Transaction
import org.shadowrunrussia2020.android.models.billing.Transfer
import retrofit2.Response

class BillingViewModel(application: Application) : AndroidViewModel(application) {
    private val mBillingRepository = BillingRepository(
        (application as ShadowrunRussia2020Application).getRetrofit().create(BillingWebService::class.java),
        Room.databaseBuilder(
            getApplication(),
            CacheDatabase::class.java, "cache-db"
        ).fallbackToDestructiveMigration().build().billingDao()
    )

    fun getBalance(): LiveData<Int> {
        return mBillingRepository.getBalance();
    }

    fun getHistory(): LiveData<List<Transaction>> {
        return mBillingRepository.getHistory()
    }

    suspend fun refresh() {
        mBillingRepository.refresh()
    }

    suspend fun transferMoney(receiver: Int, amount: Int, comment: String?): Response<Empty> {
        return mBillingRepository.transferMoney(Transfer(receiver, amount, comment))
    }
}