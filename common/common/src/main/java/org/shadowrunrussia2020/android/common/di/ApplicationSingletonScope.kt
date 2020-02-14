package org.shadowrunrussia2020.android.common.di

import org.shadowrunrussia2020.android.common.CacheDatabase
import org.shadowrunrussia2020.android.common.Session
import org.shadowrunrussia2020.android.common.di.components.IModelSingletonComponent
import retrofit2.Retrofit
import java.lang.RuntimeException
import java.lang.ref.WeakReference



interface ApplicationSingletonScope {
    val dependency: Dependency
    val component: Components

    interface Dependency {

        val retrofit: Retrofit
        val session: Session
        val database: CacheDatabase
    }

    interface Components : IModelSingletonComponent

    @Suppress("UNCHECKED_CAST")
    object DependencyProvider {

        lateinit var app: WeakReference<ApplicationSingletonScope>

        fun <_DEPENDENCY : ApplicationSingletonScope.Dependency> provideDependency(): _DEPENDENCY = app.get()?.dependency as? _DEPENDENCY
            ?: throw RuntimeException("$ class is not provided in Application dependensy scope")

        val dependency get() = app.get()?.dependency
            ?: throw RuntimeException("$ class is not provided in Application dependensy scope")
    }

    object ComponentProvider{
        val components: ApplicationSingletonScope.Components = DependencyProvider.app.get()?.component
            ?: throw RuntimeException("Components not init now")
    }

}

