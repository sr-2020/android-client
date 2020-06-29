package org.shadowrunrussia2020.android.common.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun showErrorMessage(context: Context, msg: String) {
    Toasty.error(context, msg, Toast.LENGTH_LONG, true).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun showInfoMessage(context: Context, msg: String) {
    Toasty.info(context, msg, Toast.LENGTH_LONG, true).apply {
        show()
    }
}

fun showSuccessMessage(context: Context, msg: String) {
    Toasty.success(context, msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}
