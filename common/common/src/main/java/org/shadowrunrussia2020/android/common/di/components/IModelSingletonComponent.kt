package org.shadowrunrussia2020.android.common.di.components

import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.declaration.repository.IPositionsRepository


interface IModelSingletonComponent {
    fun clearAllTables()

    val charterRepository: ICharacterRepository
    val billingRepository: IBillingRepository
    val positionsRepository: IPositionsRepository
}