package org.shadowrunrussia2020.android

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.persistence.room.Room
import org.shadowrunrussia2020.android.models.billing.Balance
import org.shadowrunrussia2020.android.models.billing.Transaction
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BillingViewModel(application: Application) : AndroidViewModel(application) {
    private val mBillingRepository = BillingRepository(
        Retrofit.Builder()
        .baseUrl("http://192.168.178.29/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(BillingWebService::class.java),
        Room.databaseBuilder(
            getApplication(),
            CacheDatabase::class.java, "cache-db"
        ).fallbackToDestructiveMigration().build().billingDao())

    fun getBalance(): LiveData<Int> {
        return mBillingRepository.getBalance();
    }

    fun getHistory(): LiveData<List<Transaction>> {
        return mBillingRepository.getHistory()
    }

    fun refresh() {
        mBillingRepository.refresh()
    }
}