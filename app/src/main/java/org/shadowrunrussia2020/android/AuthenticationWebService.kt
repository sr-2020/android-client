package org.shadowrunrussia2020.android

import org.shadowrunrussia2020.android.models.LoginRequest
import org.shadowrunrussia2020.android.models.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

// See Swagger API documentation at http://85.143.222.113/api/documentation
interface AuthenticationWebService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Deferred<LoginResponse>
}

