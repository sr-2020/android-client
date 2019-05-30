package org.shadowrunrussia2020.android.billing.models

data class Transfer(
    var sin_to: Int,
    var amount: Int,
    var comment: String?
)