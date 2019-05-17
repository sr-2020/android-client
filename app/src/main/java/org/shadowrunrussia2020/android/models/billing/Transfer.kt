package org.shadowrunrussia2020.android.models.billing

data class Transfer(
    var sin_from: Int,
    var sin_to: Int,
    var amount: Int,
    var comment: String?
)