package org.shadowrunrussia2020.android.billing.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountOverview (
    @PrimaryKey
    var id: Int,
    var sin: Int,
    var balance: Int
)