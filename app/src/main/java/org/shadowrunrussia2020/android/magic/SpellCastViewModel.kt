package org.shadowrunrussia2020.android.magic

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SpellCastViewModel(application: Application) : AndroidViewModel(application) {
    var secondsLeft = 0
    var id = ""
    var power = 0
    var reagentIds = listOf<String>()
    var ritualMemberIds = listOf<String>()
    var focusId: String? = null
    var targetCharacterId: String? = null
}