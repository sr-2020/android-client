package org.shadowrunrussia2020.android.magic

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_spell_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.QrType
import org.shadowrunrussia2020.android.common.models.Spell
import org.shadowrunrussia2020.android.common.utils.russianQrType
import org.shadowrunrussia2020.android.common.utils.russianSpellSphere
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.positions.PositionsWebService
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.startQrScan

class SpellDetailsFragment : Fragment() {
    private val args: SpellDetailsFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val mCharacterModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    private val mModel by lazy {
        ViewModelProviders.of(this).get(SpellDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spell_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAbilityName.text = spell.humanReadableName
        textAbilityDescription.text = spell.description
        textAbilitySphere.text = russianSpellSphere(spell.sphere)

        updateEnableness(true)

        castSpell.setOnClickListener {
            findNavController().navigate(
                SpellDetailsFragmentDirections.actionStartCast(spell, mModel.power)
            )
        }

        addFocus.setOnClickListener {
            startQrScan(this, "Указание фокуса.")
        }

        seekBarSpellPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateEnableness(progress > 0)
                mModel.power = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mCharacterModel.getCharacter()
            .observe(this, Observer { data: Character? ->
                if (data != null) {
                    mModel.maxCharacterPower = data.magic + data.magicStats.maxPowerBonus
                    textCurrentMagic.text = "Текущее значение магии: ${data.magic.toString()}"
                }
                updateMaxPower()
            })

        CoroutineScope(Dispatchers.IO).launch {
            val d: ApplicationSingletonScope.Dependency = ApplicationSingletonScope.DependencyProvider.provideDependency()
            val service = d.retrofit.create(PositionsWebService::class.java)
            try {
                val response = service.manalevel().await()
                var manaLevel = response.body()!!.manaLevel;
                val levelToBonus = listOf(-2, -2, -1, 0, 1, 1, 2, 3, 4, 5, 6)
                if (manaLevel >= levelToBonus.size) manaLevel = levelToBonus.size
                mModel.powerLocationBonus = levelToBonus[manaLevel]
                withContext(Dispatchers.Main) {
                    updateMaxPower()
                }
            } catch (e: Error) {}
        }
    }

    private fun updateEnableness(enable: Boolean) {
        castSpell.isEnabled = enable
    }

    private fun updateMaxPower() {
        seekBarSpellPower.max = mModel.maxCharacterPower + mModel.powerFocusBonus + mModel.powerLocationBonus
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, { fullQrData ->
            if (fullQrData.type != QrType.focus) {
                showErrorMessage(requireContext(), "Неожиданный QR-код: ${russianQrType(fullQrData.type)}. Подходящие: Фокус.");
                return@maybeQrScanned
            }

            val sphere = fullQrData.data.sphere
            val bonus = fullQrData.data.amount

            if (sphere == null || bonus == null) {
                showErrorMessage(requireContext(), "Некорректный фокус");
                return@maybeQrScanned
            }

            if (fullQrData.data.sphere != spell.sphere) {
                showErrorMessage(requireContext(), "Этот фокус не подходит к данному заклинанию");
                return@maybeQrScanned
            }

            mModel.powerFocusBonus = bonus
            addFocus.text = "Фокус (${bonus})"
            updateMaxPower()
        })
    }
}
