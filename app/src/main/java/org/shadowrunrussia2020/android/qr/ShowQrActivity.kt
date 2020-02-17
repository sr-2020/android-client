package org.shadowrunrussia2020.android.qr

import android.app.Activity
import android.os.Bundle
import androidx.navigation.navArgs
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_show_qr.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.utils.encode

class ShowQrActivity : Activity() {
    private val args: ShowQrActivityArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_qr)

        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(encode(args.qrData), BarcodeFormat.QR_CODE, 400, 400)
        qrCodeImage.setImageBitmap(bitmap)
    }
}
