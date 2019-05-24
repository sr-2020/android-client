package org.shadowrunrussia2020.android

import com.google.gson.Gson
import okhttp3.Interceptor
import java.io.IOException


data class ErrorMessage(val message: String)
data class Error(val error: ErrorMessage)

class AuthorizationInterceptor(private val session: Session) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val authorizedRequest = chain.request().newBuilder()
            .header("Authorization", "Bearer ${session.getToken()}")
            .build()
        return chain.proceed(authorizedRequest)
    }
}

class TestSuccessInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        var response = chain.proceed(request)

        if (response.isSuccessful) {
            return response
        } else {
            // TODO(aeremin) If response.code() is 401/403 (Unauthorized) - force a logout
            throw IOException(getExceptionMessage(response.body()!!.string()))
        }
    }

    private fun getExceptionMessage(body: String): String {
        return try {
            Gson().fromJson(body, Error::class.java).error.message
        } catch (e: Exception) {
            "Некорректный ответ сервера"
        }
    }
}
