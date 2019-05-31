package org.shadowrunrussia2020.android.qr


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.billing.models.Transfer

class ScanQrFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(
            "Сканирование QR-кода. Для включения/выключения подсветки вспышкой используйте кнопки регулировки громкости.")
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val qrData = maybeProcessActivityResult(activity!!, requestCode, resultCode, data)
            ?: return super.onActivityResult(requestCode, resultCode, data)
        when (qrData.type) {
            Type.PAYMENT_REQUEST_WITH_PRICE -> {
                try {
                    val parts = qrData.payload.split(',', limit = 3)
                    val d = ScanQrFragmentDirections.actionBillScanned(
                        Transfer(
                            sin_to = parts[0].toInt(),
                            amount = parts[1].toInt(),
                            comment = parts[2]
                        )
                    )
                    findNavController().navigate(d)
                    return
                } catch (e: Exception) {
                    Toast.makeText(
                        activity!!,
                        "Неподдерживаемый QR-код",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {
                Toast.makeText(
                    activity!!,
                    "Содержимое QR-кода: ${qrData.type}, ${qrData.payload}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        findNavController().navigate(R.id.action_global_back_to_main)
    }
}
