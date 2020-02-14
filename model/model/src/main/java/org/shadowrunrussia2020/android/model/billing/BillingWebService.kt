package org.shadowrunrussia2020.android.model.billing

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.common.models.AccountInfo
import org.shadowrunrussia2020.android.common.models.Empty
import org.shadowrunrussia2020.android.common.models.Transfer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BillingWebService {
    @POST("billing/transfer")
    fun transfer(@Body request: Transfer): Deferred<Response<Empty>>

    @GET("billing/account_info")
    fun accountInfo(): Deferred<Response<AccountInfo>>
}


