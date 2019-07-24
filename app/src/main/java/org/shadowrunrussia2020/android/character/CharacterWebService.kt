package org.shadowrunrussia2020.android.character

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.character.models.CharacterResponse
import org.shadowrunrussia2020.android.character.models.Event
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CharacterWebService {
    @GET("models-manager/character/model/{id}")
    fun get(@Path("id") id: String): Deferred<Response<CharacterResponse>>

    @POST("models-manager/character/model/{id}")
    fun postEvent(@Path("id") id: String, @Body request: Event): Deferred<Response<CharacterResponse>>
}