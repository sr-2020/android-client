package org.shadowrunrussia2020.android.model.qr

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.R

fun startQrScan(parent: Fragment, message: String) {
    IntentIntegrator.forSupportFragment(parent)
        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        .setPrompt("$message ${parent.getString(R.string.scan_qr_generic)}")
        .setBeepEnabled(false)
        .initiateScan()
}

fun maybeQrScanned(parent: Activity, requestCode: Int, resultCode: Int, data: Intent?,
                   onQrScanned: suspend (FullQrData) -> Unit,
                   onScanCancelled: () -> Unit = {}) {
    val contents = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents
        ?: return onScanCancelled()

    CoroutineScope(Dispatchers.Main).launch {
        try {
            val qrData = decode(contents)
            val fullQrData = retrieveQrData(qrData)
            Log.i("QR", "name = ${fullQrData.name}, type = ${fullQrData.type}, id = ${fullQrData.modelId}")
            onQrScanned(fullQrData)
        } catch (e: ValidationException) {
            showErrorMessage(parent, "Неподдерживаемый QR-код")
        } catch (e: FormatException) {
            showErrorMessage(parent, "Неподдерживаемый QR-код")
        } catch (e: ExpiredException) {
            showErrorMessage(parent, "Срок действия QR-кода истек")
        } catch (e: Exception) {
            showErrorMessage(parent, "${e.message}")
        }
        onScanCancelled()
    }
}