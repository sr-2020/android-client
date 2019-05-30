package org.shadowrunrussia2020.android.models.billing

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

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
