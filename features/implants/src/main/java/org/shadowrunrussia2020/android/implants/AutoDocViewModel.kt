package org.shadowrunrussia2020.android.implants

import androidx.lifecycle.ViewModel

class AutoDocViewModel: ViewModel() {
    enum class State { WAITING_FOR_BODY_SCAN, IDLE, WAITING_FOR_EMPTY_QR_SCAN, WAITING_FOR_IMPLANT_QR_SCAN }
    public var state = State.WAITING_FOR_BODY_SCAN
    public var targetCharacterId: String? = null;
    public var implantToRemove: String? = null;
}