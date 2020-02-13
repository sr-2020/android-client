package org.shadowrunrussia2020.android.common

import android.content.SharedPreferences

class Session(private val preferences: SharedPreferences) {
    private var token: String? = preferences.getString("backend_token", null)
    private var characterId: Int = preferences.getInt("character_id", 0)

    fun getToken(): String? {
        return token
    }

    fun getCharacterId(): Int? {
        return if (characterId != 0) characterId else null
    }

    fun setTokenAndId(token: String, characterId: Int) {
        this.token = token
        this.characterId = characterId
        preferences.edit()
            .putString("backend_token", token)
            .putInt("character_id", characterId)
            .apply()
    }

    fun invalidate() {
        this.token = null
        this.characterId = 0
        preferences.edit()
            .remove("backend_token")
            .remove("character_id")
            .apply()
    }
}
