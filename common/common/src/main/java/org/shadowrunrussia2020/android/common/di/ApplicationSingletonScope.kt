package org.shadowrunrussia2020.android.common.di

import android.content.Context
import org.shadowrunrussia2020.android.common.Session
import org.shadowrunrussia2020.android.common.di.components.IModelSingletonComponent
import retrofit2.Retrofit
import java.lang.ref.WeakReference

interface ApplicationSingletonScope {
    val dependency: Dependency
    val component: Components

    interface Dependency {
        val retrofit: Retrofit
        val qrRetrofit: Retrofit
        val session: Session
    }

    interface Components : IModelSingletonComponent

    @Suppress("UNCHECKED_CAST")
    object DependencyProvider {

        lateinit var app: WeakReference<ApplicationSingletonScope>

        fun <_DEPENDENCY : Dependency> provideDependency(): _DEPENDENCY = app.get()?.dependency as? _DEPENDENCY
            ?: throw RuntimeException("$ class is not provided in Application scope")

        val dependency
            get() = app.get()?.dependency
                ?: throw RuntimeException("$ class is not provided in Application scope")
    }

    object ComponentProvider {
        val components: Components = DependencyProvider.app.get()?.component
            ?: throw RuntimeException("Components not init now")
    }

    object ContextProvider {
        lateinit var providedAppContext: WeakReference<Context>
        val requireContext: Context
            get() = providedAppContext.get()
                ?: throw RuntimeException("Application context is not available now")
    }

}

