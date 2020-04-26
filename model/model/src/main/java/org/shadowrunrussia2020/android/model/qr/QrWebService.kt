package org.shadowrunrussia2020.android.model.qr

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QrWebService {
    @GET("{id}")
    fun get(@Path("id") id: String): Deferred<Response<QrResponse>>
}
