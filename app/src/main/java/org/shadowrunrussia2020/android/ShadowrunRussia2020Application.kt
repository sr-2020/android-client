package org.shadowrunrussia2020.android

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.bugfender.sdk.Bugfender
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.shadowrunrussia2020.android.common.CacheDatabase
import org.shadowrunrussia2020.android.common.Session
import org.shadowrunrussia2020.android.di.IApplicationSingletonDi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShadowrunRussia2020Application : Application(), IApplicationSingletonDi {
    override val session by lazy { Session(getGlobalSharedPreferences()) }
    override val retrofit: Retrofit by lazy { createRetrofit() }
    override val database by lazy { createDatabase() }

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

    private fun baseRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(createClient())
    }

    private fun createRetrofit(): Retrofit {
        return baseRetrofitBuilder()
            .baseUrl(getBackendUrl(this, this))
            .build()
    }

    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(session))
            .addInterceptor(TestSuccessInterceptor(session))
            .build()
    }

    private fun createDatabase(): CacheDatabase {
        return Room.databaseBuilder(
            this,
            CacheDatabase::class.java, "cache-db"
        ).fallbackToDestructiveMigration().build()
    }
}
