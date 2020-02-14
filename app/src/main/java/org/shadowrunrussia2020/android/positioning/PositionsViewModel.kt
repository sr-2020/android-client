package org.shadowrunrussia2020.android.positioning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.ShadowrunRussia2020Application
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Position

class PositionsViewModel(application: Application) : AndroidViewModel(application) {
    private val dependency = ApplicationSingletonScope.DependencyProvider.provideDependency<ApplicationSingletonScope.Dependency>()


    private val mBillingRepository = PositionsRepository(dependency.retrofit.create(PositionsWebService::class.java), dependency.database.positionsDao())

    fun positions(): LiveData<List<Position>> {
        return mBillingRepository.positions()
    }

    suspend fun refresh() {
        mBillingRepository.refresh()
    }
}