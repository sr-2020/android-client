package org.shadowrunrussia2020.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.shadowrunrussia2020.android.qr.Data
import org.shadowrunrussia2020.android.qr.encode

fun startShowQrActivity(parent: Activity, data: Data) {
    var intent = Intent(parent, ShowQrActivity::class.java)
    intent.putExtra("QR_CONTENT", encode(data))
    parent.startActivity(intent)
}

class ShowQrActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_qr)

        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(intent.getStringExtra("QR_CONTENT"), BarcodeFormat.QR_CODE, 400, 400)
        val imageViewQrCode = findViewById<ImageView>(R.id.qrCodeImage)
        imageViewQrCode.setImageBitmap(bitmap)
    }
}
