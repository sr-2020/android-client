package org.shadowrunrussia2020.android.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment:PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main_preferences)
    }


}