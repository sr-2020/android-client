package org.shadowrunrussia2020.android

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShadowrunRussia2020Application : Application() {
    private val mSession by lazy { Session(getGlobalSharedPreferences()) }
    private val mRetrofit by lazy { createRetrofit() }
    private val mDatabase by lazy { createDatabase() }

    fun getGlobalSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    fun getSession(): Session {
        return mSession
    }

    fun getRetrofit(): Retrofit {
        return mRetrofit
    }

    fun getDatabase(): CacheDatabase {
        return mDatabase
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
            .addInterceptor(AuthorizationInterceptor(getSession()))
            .addInterceptor(TestSuccessInterceptor())
            .build()
    }

    private fun createDatabase(): CacheDatabase {
        return Room.databaseBuilder(
            this,
            CacheDatabase::class.java, "cache-db"
        ).fallbackToDestructiveMigration().build()
    }
}
