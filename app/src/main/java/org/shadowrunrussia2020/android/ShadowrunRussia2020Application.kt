package org.shadowrunrussia2020.android

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShadowrunRussia2020Application : Application() {
    private val mSession by lazy { Session(getGlobalSharedPreferences()) }
    private val mRetrofit by lazy { createRetrofit() }

    fun getGlobalSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    fun getSession(): Session {
        return mSession
    }

    fun getRetrofit(): Retrofit {
        return mRetrofit
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.178.29/")
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
}
