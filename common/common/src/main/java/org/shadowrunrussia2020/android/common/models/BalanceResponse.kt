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
    var sin: String,
    var personName: String,
    var currentBalance: Float,
    var currentScoring: Float,
    var sumRents: Float,
    var lifeStyle: String,
    var forecastLifeStyle: String,
    var metatype: String,
    var citizenship: String,
    var insurance: String,
    var pledgee: String,
    var viza: String,
    var licenses: List<String>
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
    var amount: Float,
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

@Parcelize
@Entity
data class Rent (
    @PrimaryKey
    var rentaId: Int,
    var finalPrice: Float,
    var productType: String,
    var nomenklaturaName: String,
    var skuName: String,
    var corporation: String,
    var shop: String,
    var dateCreated: Date
) : Parcelable

data class RentsData (
    var rentas: List<Rent>,
    var sum: Float
)

data class RentsResponse (
    var data: RentsData
)
