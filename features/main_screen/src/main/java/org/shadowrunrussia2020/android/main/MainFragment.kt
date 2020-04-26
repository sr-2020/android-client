package org.shadowrunrussia2020.android.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.main_charter_screen.*
import org.shadowrunrussia2020.android.common.utils.encode
import org.shadowrunrussia2020.android.common.utils.qrData

class MainFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_charter_screen, container, false)

    override fun onResume() {
        super.onResume()

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->
                textHp.text = "%s♥".format(character.maxHp)
                textEssence.text = "%s✡".format(character.essence / 100)

                fullTextHP.text = "Максимальные хиты: %s♡".format(character.maxHp)
                fullTextEssence.text = "Эссенс: %s✡".format(character.essence / 100)

                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(encode(character.qrData), BarcodeFormat.QR_CODE, 400, 400)
                qrDataView.setImageBitmap(bitmap)
            }
        }
    }
}

