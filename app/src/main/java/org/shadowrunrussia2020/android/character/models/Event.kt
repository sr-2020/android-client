package org.shadowrunrussia2020.android.character.models

data class Event(val eventType: String, val data: Map<String, Int> = hashMapOf())