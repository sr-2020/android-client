package org.shadowrunrussia2020.android.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.main_character_view_item.view.*
import kotlinx.android.synthetic.main.main_charter_screen.*
import org.shadowrunrussia2020.android.common.utils.encode
import org.shadowrunrussia2020.android.common.utils.qrData

class MainFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_charter_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.observe({ this.lifecycle }) {
            it?.let {
                textHp.text = "%s HP".format(it.maxHp)
                textMana.text = "%s MP".format(it.magic)
                textPowerBonus.text = "Power +%s ".format(it.magicPowerBonus)
                textStatus.text = " %s ".format(it.healthState)

                statuses.removeAllViews()

                it.passiveAbilities.forEach { ability ->
                    View.inflate(context, R.layout.main_character_view_item, statuses).apply {
                        textHeader.text = ability.name
                        textSource.text = ability.description
                    }

                }

                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(encode(it.qrData), BarcodeFormat.QR_CODE, 400, 400)
                qrDataView.setImageBitmap(bitmap)
            }
        }




    }


}