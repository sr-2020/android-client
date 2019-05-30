package org.shadowrunrussia2020.android.billing.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey
    var id: Int,
    var balance: Int
)