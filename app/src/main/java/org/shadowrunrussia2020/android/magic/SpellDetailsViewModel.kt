package org.shadowrunrussia2020.android.magic

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SpellDetailsViewModel(application: Application) : AndroidViewModel(application) {
    var power = 1
    var maxCharacterPower = 0
    var powerFocusBonus = 0
    var powerLocationBonus = -2
    var focusId: String? = null
}