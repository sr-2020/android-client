package org.shadowrunrussia2020.android.positioning

import android.util.Log
import androidx.lifecycle.LiveData
import org.shadowrunrussia2020.android.common.declaration.PositionsDao
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.common.models.UserResponse
import org.shadowrunrussia2020.android.common.models.fromResponse
import retrofit2.Response

class PositionsRepository(
    private val mService: PositionsWebService,
    private val mPositionsDao: PositionsDao
) {

    suspend fun refresh() {
        saveToDao(mService.users().await())
    }

    fun positions(): LiveData<List<Position>> {
        return mPositionsDao.positions()
    }

    suspend fun sendBeacons(request: PositionsRequest) {
        mService.positions(request).await()
        refresh()
    }

    private fun saveToDao(response: Response<List<UserResponse>>) {
        val positions = response.body()
        if (positions == null) {
            Log.e("PositionsRepository", "Invalid server response - body is empty")
        } else {
            mPositionsDao.insertPositions(positions.filter { it.location != null }.map { fromResponse(it) })
        }
    }
}