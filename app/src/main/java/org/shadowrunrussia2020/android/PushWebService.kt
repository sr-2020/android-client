package org.shadowrunrussia2020.android

import kotlinx.coroutines.Deferred
import org.shadowrunrussia2020.android.common.models.Empty
import org.shadowrunrussia2020.android.common.models.SaveTokenRequest
import retrofit2.http.Body
import retrofit2.http.PUT

interface PushWebService {
    @PUT("save_token")
    fun saveToken(@Body request: SaveTokenRequest): Deferred<Empty>
}

