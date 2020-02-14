package org.shadowrunrussia2020.android.di

import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.di.components.IModelSingletonComponent
import org.shadowrunrussia2020.android.model.di.ModelSingletonComponent

class ApplicationComponents():
    ApplicationSingletonScope.Components,
    IModelSingletonComponent by ModelSingletonComponent()