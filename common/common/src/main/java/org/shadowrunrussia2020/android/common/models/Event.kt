package org.shadowrunrussia2020.android.common.models

data class Event(val eventType: String, val data: HashMap<String, Any> = hashMapOf())