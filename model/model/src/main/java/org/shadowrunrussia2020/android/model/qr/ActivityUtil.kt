package org.shadowrunrussia2020.android.model.qr

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.R

fun startQrScan(parent: Fragment, message: String) {
    IntentIntegrator.forSupportFragment(parent)
        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        .setPrompt("$message ${parent.getString(R.string.scan_qr_generic)}")
        .setBeepEnabled(false)
        .initiateScan()
}

fun maybeProcessActivityResult(parent: Activity, requestCode: Int, resultCode: Int, data: Intent?): Data? {
    val contents = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents ?: return null

    try {
        return decode(contents)
    } catch (e: ValidationException) {
        showErrorMessage(
            parent,
            "Неподдерживаемый QR-код"
        )
    } catch (e: FormatException) {
        showErrorMessage(
            parent,
            "Неподдерживаемый QR-код"
        )
    } catch (e: ExpiredException) {
        showErrorMessage(
            parent,
            "Срок действия QR-кода истек"
        )
    }
    return null
}
