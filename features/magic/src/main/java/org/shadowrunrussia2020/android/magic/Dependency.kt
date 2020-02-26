package org.shadowrunrussia2020.android.magic

import org.shadowrunrussia2020.android.common.declaration.repository.IBillingRepository
import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.declaration.repository.IPositionsRepository
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.di.MainActivityScope

//interface ActivityScopeMainDependency : MainActivityScope

interface MagicScreenDependency: ApplicationSingletonScope.Dependency{
    val characterRepository: ICharacterRepository
//    val billingRepository: IBillingRepository
//    val positionsRepository: IPositionsRepository
}