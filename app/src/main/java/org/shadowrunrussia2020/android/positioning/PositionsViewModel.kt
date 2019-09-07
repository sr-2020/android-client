package org.shadowrunrussia2020.android.positioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.ShadowrunRussia2020Application

class PositionsViewModel(application: Application) : AndroidViewModel(application) {
    private val mBillingRepository = PositionsRepository(
        (application as ShadowrunRussia2020Application).getRetrofit().create(PositionsWebService::class.java),
        application.getDatabase().positionsDao()
    )

    fun positions(): LiveData<List<Position>> {
        return mBillingRepository.positions()
    }

    suspend fun refresh() {
        mBillingRepository.refresh()
    }
}