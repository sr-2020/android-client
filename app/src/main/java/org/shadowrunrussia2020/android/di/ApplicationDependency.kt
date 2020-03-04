package org.shadowrunrussia2020.android.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.shadowrunrussia2020.android.AuthorizationInterceptor
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.TestSuccessInterceptor
import org.shadowrunrussia2020.android.common.Session
import org.shadowrunrussia2020.android.common.declaration.repository.ICharacterRepository
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.magic.MagicScreenDependency
import org.shadowrunrussia2020.android.main.MainScreenDependency
import org.shadowrunrussia2020.android.model.di.ModelDependency
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference

interface IApplicationDependency :
    ApplicationSingletonScope.Dependency,
    MainScreenDependency,
    MagicScreenDependency,
    ModelDependency

class ApplicationDependency(val applicationProvider: WeakReference<Application>) : IApplicationDependency {

    override val session by lazy { Session(getGlobalSharedPreferences()) }
    override val retrofit: Retrofit by lazy { createRetrofit() }

    val application: Application
        get() = applicationProvider.get()
            ?: throw RuntimeException("You call method when application class will be destroyed")

    fun getGlobalSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    private fun baseRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(createClient())
    }

    private fun createRetrofit(): Retrofit {
        return baseRetrofitBuilder()
            .baseUrl(application.getString(R.string.backend_url))
            .build()
    }

    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(session))
            .addInterceptor(TestSuccessInterceptor(session))
            .build()
    }

    override val characterRepository: ICharacterRepository
        get() = ApplicationSingletonScope.ComponentProvider.components.charterRepository
}