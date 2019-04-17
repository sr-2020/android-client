package org.shadowrunrussia2020.android

import `in`.aerem.comconbeacons.models.LoginRequest
import `in`.aerem.comconbeacons.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// See Swagger API documentation at http://85.143.222.113/api/documentation
interface AuthenticationWebService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}

