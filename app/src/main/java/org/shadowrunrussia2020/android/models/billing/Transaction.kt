package org.shadowrunrussia2020.android.models.billing

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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
)
