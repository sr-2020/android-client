package org.shadowrunrussia2020.android.common.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
data class AccountOverview (
    @PrimaryKey
    var id: Int,
    var characterId: Int,
    var currentBalance: Float,
    var currentScoring: Float,
    var lifeStyle: String,
    var forecastLifeStyle: String
)

data class BalanceResponse(
    var data: AccountOverview
)

@Parcelize
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var operationTime: Date,
    val from: String,
    val to: String,
    var amount: Int,
    var comment: String?,
    var transferType: String
) : Parcelable

data class TransfersResponse (
    var data: List<Transaction>
)

@Parcelize
data class Transfer(
    var sin_to: Int,
    var amount: Int,
    var comment: String?
) : Parcelable
