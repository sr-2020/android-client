package org.shadowrunrussia2020.android.model.positions

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.common.models.ManaLevelResponse
import org.shadowrunrussia2020.android.common.models.PositionsRequest
import org.shadowrunrussia2020.android.common.models.PositionsResponse
import org.shadowrunrussia2020.android.common.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PositionsWebService {
    @POST("position/positions")
    fun positions(@Body request: PositionsRequest): Deferred<Response<PositionsResponse>>

    @GET("position/manalevel")
    fun manalevel(): Deferred<Response<ManaLevelResponse>>

    @GET("profile")
    fun users():  Deferred<Response<List<UserResponse>>>
}

