package org.shadowrunrussia2020.android.model.billing

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.common.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface BillingWebService {
    @POST("billing/transfer")
    fun transfer(@Body request: Transfer): Deferred<Response<Empty>>

    @GET("billing/sin")
    fun balance(): Deferred<Response<BalanceResponse>>

    @GET("billing/transfers")
    fun transfers(): Deferred<Response<TransfersResponse>>

    @GET("billing/rentas")
    fun rents(): Deferred<Response<RentsResponse>>
}


