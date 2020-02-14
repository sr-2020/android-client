package org.shadowrunrussia2020.android.settings

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    val dependency
        get() = (activity as? ActivityScopeSettingsDependency)
            ?: throw RuntimeException("SettingsFragment must be attached to Maint Activity, and main activity ust implement it dependency")

    val logoutPreference by lazy { findPreference<Preference>("logout") }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main_preferences)

        logoutPreference?.setOnPreferenceClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Вы уверены что хотите выйти?")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    dependency.exit()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
            true
        }

    }


}

