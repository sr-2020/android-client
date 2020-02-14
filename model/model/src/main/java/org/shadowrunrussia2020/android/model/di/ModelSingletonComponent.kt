package org.shadowrunrussia2020.android.model.di

import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.di.components.IModelSingletonComponent
import org.shadowrunrussia2020.android.model.CacheDatabase
import org.shadowrunrussia2020.android.model.billing.BillingRepository
import org.shadowrunrussia2020.android.model.billing.BillingWebService
import org.shadowrunrussia2020.android.model.charter.CharacterRepository
import org.shadowrunrussia2020.android.model.charter.CharacterWebService
import org.shadowrunrussia2020.android.model.positions.PositionsRepository
import org.shadowrunrussia2020.android.model.positions.PositionsWebService


class ModelSingletonComponent : IModelSingletonComponent {
    private val dependency: ModelDependency = ApplicationSingletonScope.DependencyProvider.provideDependency()

    override val charterRepository by lazy {
        CharacterRepository(dependency.retrofit.create(CharacterWebService::class.java), database.characterDao())
    }

    override val billingRepository by lazy {
        BillingRepository(dependency.retrofit.create(BillingWebService::class.java), database.billingDao())
    }

    override val positionsRepository by lazy {
        PositionsRepository(dependency.retrofit.create(PositionsWebService::class.java), database.positionsDao())
    }

    val database by lazy { CacheDatabase.build(ApplicationSingletonScope.ContextProvider.requireContext) }

    override fun clearAllTables()  = database.clearAllTables()
}