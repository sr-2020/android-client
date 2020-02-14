package org.shadowrunrussia2020.android.model.di

import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.declaration.repository.IPositionsRepository
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

    override val charterRepository: ICharacterRepository by lazy {
        CharacterRepository(dependency.retrofit.create(CharacterWebService::class.java), database.characterDao())
    }

    override val billingRepository: IBillingRepository by lazy {
        BillingRepository(dependency.retrofit.create(BillingWebService::class.java), database.billingDao())
    }

    override val positionsRepository: IPositionsRepository by lazy {
        PositionsRepository(dependency.retrofit.create(PositionsWebService::class.java), database.positionsDao())
    }

    private val database by lazy { CacheDatabase.build(ApplicationSingletonScope.ContextProvider.requireContext) }

    override fun clearAllTables()  = database.clearAllTables()
}