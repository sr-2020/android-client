package org.shadowrunrussia2020.android.qr


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import org.shadowrunrussia2020.android.R

class ScanQrFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.data = QrDataOrError(qrData = null, error = false)
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(
            "Сканирование QR-кода. Для включения/выключения подсветки вспышкой используйте кнопки регулировки громкости."
        )
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val qrData = maybeProcessActivityResult(activity!!, requestCode, resultCode, data)
        viewModel.data = QrDataOrError(qrData = qrData, error = (qrData == null));
        findNavController().popBackStack()
    }
}
