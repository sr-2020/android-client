package org.shadowrunrussia2020.android.magic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.shadowrunrussia2020.android.common.models.ReagentContent

enum class QrReason {
    REAGENT,
    RITUAL_MEMBER,
    RITUAL_VICTIM,
    TARGET,
}

class SpellCastViewModel(application: Application) : AndroidViewModel(application) {
    var id = ""
    var power = 0
    var reagentIds = mutableSetOf<String>()
    var ritualMembersIds = mutableSetOf<String>()
    var ritualVictimIds = mutableSetOf<String>()
    var focusId: String? = null
    var targetCharacterId: String? = null
    var qrReason: QrReason? = null
    var reagentContent: ReagentContent = ReagentContent()
}