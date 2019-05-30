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
    // TODO(aeremin): Merge retrofits when backend addresses will be same
    private val mRetrofit by lazy { createRetrofit() }
    private val mAuthRetrofit by lazy { createAuthRetrofit() }
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

    fun getAuthRetrofit(): Retrofit {
        return mAuthRetrofit
    }

    fun getDatabase(): CacheDatabase {
        return mDatabase
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://gateway.evarun.ru/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(createClient())
            .build()
    }

    private fun createAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getBackendUrl(this, this))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(createClient())
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
