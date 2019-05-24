package org.shadowrunrussia2020.android

import android.content.SharedPreferences

class Session(private val preferences: SharedPreferences) {
    private var token: String? = preferences.getString("backend_token", null)

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
        preferences.edit().putString("backend_token", token).apply()
    }

    fun invalidate() {
        this.token = null
        preferences.edit().remove("backend_token").apply()
    }
}