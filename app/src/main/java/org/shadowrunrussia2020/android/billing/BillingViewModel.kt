package org.shadowrunrussia2020.android.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.ShadowrunRussia2020Application
import org.shadowrunrussia2020.android.billing.models.AccountOverview
import org.shadowrunrussia2020.android.models.Empty
import org.shadowrunrussia2020.android.billing.models.Transaction
import org.shadowrunrussia2020.android.billing.models.Transfer
import retrofit2.Response

class BillingViewModel(application: Application) : AndroidViewModel(application) {
    private val mBillingRepository = BillingRepository(
        (application as ShadowrunRussia2020Application).getRetrofit().create(BillingWebService::class.java),
        application.getDatabase().billingDao()
    )

    fun getAccountOverview(): LiveData<AccountOverview> {
        return mBillingRepository.getAccountOverview()
    }

    fun getHistory(): LiveData<List<Transaction>> {
        return mBillingRepository.getHistory()
    }

    suspend fun refresh() {
        mBillingRepository.refresh()
    }

    suspend fun transferMoney(receiver: Int, amount: Int, comment: String?): Response<Empty> {
        return mBillingRepository.transferMoney(
            Transfer(
                receiver,
                amount,
                comment
            )
        )
    }
}