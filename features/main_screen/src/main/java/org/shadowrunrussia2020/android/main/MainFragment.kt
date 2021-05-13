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
import org.shadowrunrussia2020.android.common.models.BodyType
import org.shadowrunrussia2020.android.model.qr.encode
import org.shadowrunrussia2020.android.model.qr.qrData

class MainFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_charter_screen, container, false)

    override fun onResume() {
        super.onResume()

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->
                val showEssence = character.currentBody != BodyType.drone

                textHp.text = "%s♥".format(character.maxHp)
                textEssence.text = "%s✽".format(character.essence / 100)
                textEssence.visibility = if (showEssence) View.VISIBLE else View.INVISIBLE

                fullTextHP.text = "Максимальные хиты: %s♡".format(character.maxHp)
                fullTextEssence.text = "Эссенс: %s✽".format(character.essence / 100)
                fullTextEssence.visibility = if (showEssence) View.VISIBLE else View.INVISIBLE

                fullTextKarma.text = "Кармы доступно: %s".format(character.karma.available)

                fullTextBody.text = "Тело: ${character.body}"
                fullTextIntelligence.text = "Интеллект: ${character.intelligence}"
                fullTextCharisma.text = "Харизма: ${character.charisma}"
                fullTextResonance.text = "Резонанс: ${character.resonance}"
                fullTextFading.text = "Фейдинг: ${character.hacking.fading}"
                fullTextStrength.text = "Сила: ${character.strength}"
                fullTextMagic.text = "Магия: ${character.magic}"

                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(encode(character.qrData), BarcodeFormat.QR_CODE, 400, 400)
                qrDataView.setImageBitmap(bitmap)
            }
        }
    }
}

