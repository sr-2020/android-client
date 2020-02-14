package org.shadowrunrussia2020.android.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.AccountOverview
import org.shadowrunrussia2020.android.common.models.Transaction

class BillingViewModel(application: Application) : AndroidViewModel(application) {

    private val mBillingRepository = ApplicationSingletonScope.ComponentProvider.components.billingRepository

    fun getAccountOverview(): LiveData<AccountOverview> {
        return mBillingRepository.getAccountOverview()
    }

    fun getHistory(): LiveData<List<Transaction>> {
        return mBillingRepository.getHistory()
    }

    suspend fun refresh() {
        // TODO(aeremin) Switch to use new backend and re-enable
        // mBillingRepository.refresh()
    }

    suspend fun transferMoney(receiver: Int, amount: Int, comment: String?) {
        // TODO(aeremin) Switch to use new backend and re-enable
        /* return mBillingRepository.transferMoney(
            Transfer(
                receiver,
                amount,
                comment
            )
        ) */
    }
}