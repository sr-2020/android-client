package org.shadowrunrussia2020.android.common.declaration.repository

import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.common.models.PositionsRequest
import org.shadowrunrussia2020.android.common.models.UserResponse
import retrofit2.Response

interface IPositionsRepository {
    suspend fun refresh()
    fun positions(): LiveData<List<Position>>

    suspend fun sendBeacons(request: PositionsRequest)
    fun saveToDao(response: Response<List<UserResponse>>)
}