package org.shadowrunrussia2020.android

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

data class ErrorMessage(val message: String)
data class Error(val error: ErrorMessage)

class TestSuccessInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        var response = chain.proceed(request)

        if (response.isSuccessful) {
            return response
        } else {
            val message: String
            try {
                message = Gson().fromJson(response.body()!!.string(), Error::class.java).error.message
            } catch (e: Exception) {
                throw IOException("Некорректный ответ сервера")
            }
            throw IOException(message)
        }
    }
}

fun defaultRetrofitBuilder(): Retrofit.Builder {
    return Retrofit.Builder()
        .baseUrl("http://192.168.178.29/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(OkHttpClient.Builder().addInterceptor(TestSuccessInterceptor()).build())
}

fun defaultRetrofit(): Retrofit {
    return defaultRetrofitBuilder().build()
}
