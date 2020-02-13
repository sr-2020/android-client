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
    var sin: Int,
    var balance: Int
)

@Parcelize
@Entity
data class Transaction(
    @PrimaryKey
    var id: Int,
    var created_at: Date,
    var sin_from: Int,
    var sin_to: Int,
    var amount: Int,
    var comment: String?,
    var recurrent_payment_id: Int?
) : Parcelable

@Parcelize
data class Transfer(
    var sin_to: Int,
    var amount: Int,
    var comment: String?
) : Parcelable

data  class AccountInfo(var balance: Int, var sin: Int, var history: List<Transaction>)