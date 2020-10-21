package org.shadowrunrussia2020.android.model.charter

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.Feature
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CharacterWebService {
    @GET("models-manager/character/model")
    fun get(): Deferred<Response<CharacterResponse>>

    @GET("models-manager/character/available_features")
    fun availableFeatures(): Deferred<Response<List<Feature>>>

    @POST("models-manager/character/model")
    fun postEvent(@Body request: Event): Deferred<Response<CharacterResponse>>
}