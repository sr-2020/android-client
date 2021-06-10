package org.shadowrunrussia2020.android

import com.google.gson.Gson
import okhttp3.Interceptor
import org.shadowrunrussia2020.android.common.Session
import java.io.IOException


data class ErrorMessage(val message: String)
data class Error(val error: ErrorMessage?, val message: String?)

class AuthorizationInterceptor(private val session: Session) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val authorizedRequest = chain.request().newBuilder()
            .header("Authorization", "Bearer ${session.getToken()}")
            .build()
        return chain.proceed(authorizedRequest)
    }
}

class TestSuccessInterceptor(private val session: Session) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.isSuccessful) {
            return response
        } else {
            if (response.code() == 401 || response.code() == 403) {
                session.invalidate()
            }
            throw IOException(getExceptionMessage(response.body()!!.string()))
        }
    }

    private fun getExceptionMessage(body: String): String {
        return try {
            val error = Gson().fromJson(body, Error::class.java)
            if (error.error != null) return error.error.message
            if (error.message != null) return error.message
            return "Некорректный ответ сервера"
        } catch (e: Exception) {
            "Некорректный ответ сервера"
        }
    }
}
