package org.shadowrunrussia2020.android.model.qr

import android.app.Application
import androidx.lifecycle.AndroidViewModel

data class QrDataOrError(val qrData: org.shadowrunrussia2020.android.model.qr.Data?, val error: Boolean)

class QrViewModel(application: Application) : AndroidViewModel(application) {
    var data = QrDataOrError(
        qrData = null,
        error = false
    );
}