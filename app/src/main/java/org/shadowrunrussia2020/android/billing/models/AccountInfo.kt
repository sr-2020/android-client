package org.shadowrunrussia2020.android.billing.models

data  class AccountInfo(var balance: Int, var sin: Int, var history: List<Transaction>)