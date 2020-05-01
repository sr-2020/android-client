package org.shadowrunrussia2020.android.magic

import android.app.Application
import androidx.lifecycle.AndroidViewModel

enum class QrReason {
    REAGENT,
    RITUAL_MEMBER,
    TARGET,
}

class SpellCastViewModel(application: Application) : AndroidViewModel(application) {
    var secondsLeft = 0
    var id = ""
    var power = 0
    var reagentIds = mutableSetOf<String>()
    var ritualMembersIds = mutableSetOf<String>()
    var focusId: String? = null
    var targetCharacterId: String? = null
    var qrReason: QrReason? = null
}