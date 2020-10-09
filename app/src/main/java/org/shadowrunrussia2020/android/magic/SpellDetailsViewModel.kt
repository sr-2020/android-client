package org.shadowrunrussia2020.android.magic

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SpellDetailsViewModel(application: Application) : AndroidViewModel(application) {
    var power = 1
    var focusBonus = 0
}