package org.shadowrunrussia2020.android.model.di

import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.di.components.IModelSingletonComponent
import org.shadowrunrussia2020.android.model.charter.CharacterRepository
import org.shadowrunrussia2020.android.model.charter.CharacterWebService


class ModelSingletonComponent: IModelSingletonComponent {
    private val dependency:ModelDependency = ApplicationSingletonScope.DependencyProvider.provideDependency()

    override val charterRepository = CharacterRepository(dependency.retrofit.create(CharacterWebService::class.java),
        dependency.database.characterDao())

}