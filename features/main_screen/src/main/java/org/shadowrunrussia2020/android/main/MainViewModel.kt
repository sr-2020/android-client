package org.shadowrunrussia2020.android.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.lang.RuntimeException

internal class MainViewModel(application: Application) : AndroidViewModel(application) {

    val dependency = application as? ApplicationSingletonScopeMainDependency
        ?: RuntimeException("ApplicationSingletonScopeMAinDependency must be implement on application")



}