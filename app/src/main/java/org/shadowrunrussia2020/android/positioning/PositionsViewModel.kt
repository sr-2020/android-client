package org.shadowrunrussia2020.android.positioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.model.positions.PositionsRepository
import org.shadowrunrussia2020.android.model.positions.PositionsWebService

class PositionsViewModel(application: Application) : AndroidViewModel(application) {

    private val mBillingRepository = ApplicationSingletonScope.ComponentProvider.components.positionsRepository

    fun positions(): LiveData<List<Position>> {
        return mBillingRepository.positions()
    }

    suspend fun refresh() {
        mBillingRepository.refresh()
    }
}