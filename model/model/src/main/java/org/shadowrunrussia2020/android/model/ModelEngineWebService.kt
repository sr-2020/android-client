package org.shadowrunrussia2020.android.model

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.common.models.Reagent
import retrofit2.Response
import retrofit2.http.GET

interface ModelEngineWebService {
    @GET("reagents")
    fun reagents(): Deferred<Response<List<Reagent>>>
}