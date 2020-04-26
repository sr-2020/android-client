package org.shadowrunrussia2020.android.model.qr

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import org.shadowrunrussia2020.android.common.utils.showErrorMessage


fun maybeProcessActivityResult(parent: Activity, requestCode: Int, resultCode: Int, data: Intent?): org.shadowrunrussia2020.android.model.qr.Data? {
    val contents = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents ?: return null

    try {
        return org.shadowrunrussia2020.android.model.qr.decode(contents)
    } catch (e: org.shadowrunrussia2020.android.model.qr.ValidationException) {
        showErrorMessage(
            parent,
            "Неподдерживаемый QR-код"
        )
    } catch (e: org.shadowrunrussia2020.android.model.qr.FormatException) {
        showErrorMessage(
            parent,
            "Неподдерживаемый QR-код"
        )
    } catch (e: org.shadowrunrussia2020.android.model.qr.ExpiredException) {
        showErrorMessage(
            parent,
            "Срок действия QR-кода истек"
        )
    }
    return null
}
