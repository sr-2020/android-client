package org.shadowrunrussia2020.android.qr

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import org.shadowrunrussia2020.android.R

class ScanQrActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
    }
}
