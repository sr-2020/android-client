package org.shadowrunrussia2020.android.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.main_charter_screen.*
import org.shadowrunrussia2020.android.common.utils.LinearSpaceItemDecoration
import org.shadowrunrussia2020.android.common.utils.encode
import org.shadowrunrussia2020.android.common.utils.qrData
import org.shadowrunrussia2020.android.view.universal_list.*

class MainFragment : Fragment() {

    private val uinversalAdapter by lazy {
        UinversalAdapter().apply {
            activeAbilityList.adapter = this
            activeAbilityList.layoutManager = LinearLayoutManager(requireContext())
//            activeAbilityList.addItemDecoration(LinearSpaceItemDecoration(4))
        }
    }

    private val viewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.main_charter_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->
                textHp.text = "%s♥".format(character.maxHp)
                textMana.text = "%s✡".format(character.magic)
                textPowerBonus.text = "%s⚔".format(character.magicPowerBonus)

                uinversalAdapter.clear()
                uinversalAdapter.appendList(
                    character.spells.map { MagicSpellItem(it) as UniversalViewData }.let {
                        if(it.isNotEmpty()) it.plus(createMagicBookHeader(it)) else it
                    }
                )

                uinversalAdapter.appendList(
                    character.passiveAbilities.map { PassiveAbilitySpellItem(it) as UniversalViewData }.let {
                        if(it.isNotEmpty()) it.plus(createPassiveAbilityHeader(it)) else it
                    }
                )

                uinversalAdapter.appendList(
                    character.activeAbilities.map { ActiveAbilitySpellItem(it) as UniversalViewData }.let {
                        if(it.isNotEmpty()) it.plus(createActiveAbilityHeader(it)) else it
                    }
                )


                uinversalAdapter.apply()

                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(encode(character.qrData), BarcodeFormat.QR_CODE, 400, 400)
                qrDataView.setImageBitmap(bitmap)
            }
        }

    }


}

