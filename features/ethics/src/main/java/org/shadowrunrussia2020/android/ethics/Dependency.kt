package org.shadowrunrussia2020.android.ethics

import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope

interface EthicsScreenDependency: ApplicationSingletonScope.Dependency{
    val characterRepository: ICharacterRepository
}