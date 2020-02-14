package org.shadowrunrussia2020.android.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope

internal class MainViewModel(application: Application) : AndroidViewModel(application) {

    val dependency: MainScreenDependency = ApplicationSingletonScope.DependencyProvider.provideDependency()


}