package org.shadowrunrussia2020.android

import android.app.Application
import android.content.Context

fun getBackendUrl(application: Application, context: Context): String {
    val preferences = (application as ShadowrunRussia2020Application).getGlobalSharedPreferences()
    return preferences.getString(context.getString(R.string.backend_url_key),
        context.getString(R.string.default_backend_url))
}