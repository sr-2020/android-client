package org.shadowrunrussia2020.android.models.billing

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey
    var id: Int,
    var balance: Int
)