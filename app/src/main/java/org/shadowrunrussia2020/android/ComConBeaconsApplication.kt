package org.shadowrunrussia2020.android

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

class ShadowrunRussia2020Application : Application() {
    fun getGlobalSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }
}
