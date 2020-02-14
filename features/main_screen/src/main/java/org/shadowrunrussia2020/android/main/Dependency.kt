package org.shadowrunrussia2020.android.main

import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.di.MainActivityScope

interface ActivityScopeMainDependency : MainActivityScope

interface MainScreenDependency: ApplicationSingletonScope.Dependency{
    val characterRepository: ICharacterRepository
}