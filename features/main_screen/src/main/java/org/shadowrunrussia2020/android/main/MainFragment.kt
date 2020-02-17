package org.shadowrunrussia2020.android.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_character_view_item.view.*
import kotlinx.android.synthetic.main.main_charter_screen.*
import org.shadowrunrussia2020.android.common.models.PassiveAbility
import org.shadowrunrussia2020.android.common.utils.encode
import org.shadowrunrussia2020.android.common.utils.qrData

class MainFragment : Fragment() {

    private val abilityListAdpter by lazy {
        AbilityAdapter().apply {
            activeAbilityList.adapter = this
            activeAbilityList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private val viewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_charter_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.observe({ this.lifecycle }) {
            it?.let {
                textHp.text = "%s♥".format(it.maxHp)
                textMana.text = "%s✡".format(it.magic)
                textPowerBonus.text = "%s⚔".format(it.magicPowerBonus)
                textStatus.text = "You status: %s".format(it.healthState)

                abilityListAdpter.submitList(it.passiveAbilities)

                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(encode(it.qrData), BarcodeFormat.QR_CODE, 400, 400)
                qrDataView.setImageBitmap(bitmap)
            }
        }

    }


}

