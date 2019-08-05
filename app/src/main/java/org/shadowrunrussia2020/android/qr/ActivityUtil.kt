package org.shadowrunrussia2020.android.qr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import es.dmoral.toasty.Toasty

fun showErrorMessage(context: Context, msg: String) {
    val t = Toasty.error(context, msg, Toast.LENGTH_LONG, true).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun showInfoMessage(context: Context, msg: String) {
    val t = Toasty.info(context, msg, Toast.LENGTH_LONG, true).apply {
        show()
    }
}

fun showSuccessMessage(context: Context, msg: String) {
    val t = Toasty.success(context, msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}

fun maybeProcessActivityResult(parent: Activity, requestCode: Int, resultCode: Int, data: Intent?): Data? {
    val contents = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.contents ?: return null

    try {
        return decode(contents)
    } catch (e: ValidationException) {
        showErrorMessage(parent, "Неподдерживаемый QR-код")
    } catch (e: FormatException) {
        showErrorMessage(parent, "Неподдерживаемый QR-код")
    } catch (e: ExpiredException) {
        showErrorMessage(parent, "Срок действия QR-кода истек")
    }
    return null
}
