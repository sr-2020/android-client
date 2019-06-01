package org.shadowrunrussia2020.android.qr

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator

fun maybeProcessActivityResult(parent: Activity, requestCode: Int, resultCode: Int, data: Intent?): Data? {
    val contents = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents ?: return null

    try {
        return decode(contents)
    } catch (e: ValidationException) {
        Toast.makeText(parent, "Неподдерживаемый QR-код", Toast.LENGTH_LONG).show()
    } catch (e: FormatException) {
        Toast.makeText(parent, "Неподдерживаемый QR-код", Toast.LENGTH_LONG).show()
    } catch (e: ExpiredException) {
        Toast.makeText(parent, "Срок действия QR-кода истек", Toast.LENGTH_LONG).show()
    }
    return null
}
