package org.shadowrunrussia2020.android.magic


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
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.Spell
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.Data
import org.shadowrunrussia2020.android.model.qr.QrDataOrError
import org.shadowrunrussia2020.android.model.qr.QrViewModel
import org.shadowrunrussia2020.android.model.qr.Type

class SpellDetailsFragment : Fragment() {
    private val args: SpellDetailsFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val mCharacterModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SpellDetailsViewModel::class.java)
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

        val qrViewModel = ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java)
        val qrData = qrViewModel.data.qrData;
        qrViewModel.data = QrDataOrError(null, false)

        if (qrData != null) {
            seekBarSpellPower.progress = mModel.power
            castOnTarget(qrData)
        }

        textAbilityName.text = spell.humanReadableName
        textAbilityDescription.text = spell.description

        updateEnableness(true)

        castSpell.setOnClickListener {
            if (spell.hasTarget) chooseTarget() else castOnSelf()
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
                    seekBarSpellPower.max = data.magic
                }
            })
    }

    private fun updateEnableness(enable: Boolean) {
        castSpell.isEnabled = enable
    }

    private fun castOnSelf() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mCharacterModel.castSpell(
                        spell.id, hashMapOf("power" to seekBarSpellPower.progress)
                    )
                }

             response?.tableResponse ?.let { tableResponse->
                    findNavController().navigate(
                        SpellDetailsFragmentDirections.actionShowSpellResult(
                            tableResponse.map {
                                HistoryRecord(
                                    "", it.timestamp,
                                    "${it.spellName}, мощь: ${it.power}, откат: ${it.magicFeedback}",
                                    it.casterAura, ""
                                )
                            }.toTypedArray()
                        )
                    )
                } ?: findNavController().popBackStack()

            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                findNavController().popBackStack()
            }
        }
    }

    private fun chooseTarget() {
        findNavController().navigate(SpellDetailsFragmentDirections.actionChooseSpellTarget())
    }

    private fun castOnTarget(qrData: Data) {
        val eventData = when {
            qrData.type == Type.REWRITABLE -> hashMapOf<String, Any>(
                "qrCode" to qrData.payload.toInt(),
                "power" to seekBarSpellPower.progress
            )
            qrData.type == Type.HEALTHY_BODY || qrData.type == Type.WOUNDED_BODY -> hashMapOf<String, Any>(
                "targetCharacterId" to qrData.payload,
                "power" to seekBarSpellPower.progress
            )
            else -> {
                showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код."); return
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mCharacterModel.castSpell(spell.id, eventData)
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            findNavController().popBackStack()
        }
    }
}
