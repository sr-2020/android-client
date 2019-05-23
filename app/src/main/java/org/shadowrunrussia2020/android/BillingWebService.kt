package org.shadowrunrussia2020.android

import `in`.aerem.comconbeacons.models.LoginRequest
import `in`.aerem.comconbeacons.models.LoginResponse
import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.models.billing.AccountInfo
import org.shadowrunrussia2020.android.models.billing.Empty
import org.shadowrunrussia2020.android.models.billing.Transfer
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BillingWebService {
    @POST("transfer")
    fun transfer(@Body request: Transfer): Deferred<Response<Empty>>

    // TODO(aeremin): Inject sin? Or it will be done server-side in API Gateway?
    @GET("account_info/777")
    fun accountInfo(): Deferred<Response<AccountInfo>>
}


