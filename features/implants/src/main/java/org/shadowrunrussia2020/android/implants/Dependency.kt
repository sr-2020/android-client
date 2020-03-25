package org.shadowrunrussia2020.android.implants

import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope

interface ImplantScreensDependency: ApplicationSingletonScope.Dependency{
    val characterRepository: ICharacterRepository
}