package org.shadowrunrussia2020.android.billing.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transfer(
    var sin_to: Int,
    var amount: Int,
    var comment: String?
) : Parcelable