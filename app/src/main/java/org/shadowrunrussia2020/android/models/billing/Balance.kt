package org.shadowrunrussia2020.android.models.billing

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey
    var id: Int,
    var balance: Int
)