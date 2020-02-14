package org.shadowrunrussia2020.android

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.bugfender.sdk.Bugfender
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.di.ApplicationComponents
import org.shadowrunrussia2020.android.di.ApplicationDependency
import org.shadowrunrussia2020.android.di.IApplicationDependency
import java.lang.ref.WeakReference

class ShadowrunRussia2020Application : Application(), ApplicationSingletonScope {
    init {
        ApplicationSingletonScope.DependencyProvider.app = WeakReference(this)
    }

    override val dependency: IApplicationDependency by lazy { ApplicationDependency(WeakReference(this)) }
    override val component by lazy { ApplicationComponents() }

    override fun onCreate() {
        super.onCreate()


        Bugfender.init(this, "kg6eyTcwvJYk90uGQKXQHZjh36g85UEZ", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging()
    }

    fun getGlobalSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }


}
