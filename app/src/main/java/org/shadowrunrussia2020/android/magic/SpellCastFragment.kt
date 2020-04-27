package org.shadowrunrussia2020.android.magic


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_spell_cast.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.Spell
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.FullQrData
import org.shadowrunrussia2020.android.model.qr.Type
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.startQrScan

class SpellCastFragment : Fragment() {
    private val args: SpellCastFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val power: Int by lazy { args.power }

    private val mCharacterModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    private val castModel by lazy {
        ViewModelProviders.of(this).get(SpellCastViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spell_cast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        castModel.power = power
        castModel.id = spell.id

        textAbilityName.text = "${spell.humanReadableName} (мощь ${castModel.power})"
        textAbilityDescription.text = spell.description

        castSpell.setOnClickListener {
            if (spell.hasTarget) chooseTarget() else castOnSelf()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, {
            // TODO(aeremin) Figure out what kind of QR we are scanning
            castOnTarget(it)
        })
    }

    private fun castOnSelf() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = mCharacterModel.castSpell(
                    castModel.id, hashMapOf("power" to castModel.power)
                )

                response?.tableResponse?.let { tableResponse ->
                    findNavController().navigate(
                        SpellCastFragmentDirections.actionShowSpellResult(
                            tableResponse.map {
                                HistoryRecord(
                                    "", it.timestamp,
                                    "${it.spellName}, мощь: ${it.power}, откат: ${it.magicFeedback}",
                                    it.casterAura, ""
                                )
                            }.toTypedArray()
                        )
                    )
                } ?: goBackToSpellbook()

            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                goBackToSpellbook()
            }
        }
    }

    private fun chooseTarget() {
        startQrScan(this, "Выбор цели заклинания.")
    }

    private fun castOnTarget(qrData: FullQrData) {
        val eventData = when (qrData.type) {
            Type.HEALTHY_BODY, Type.WOUNDED_BODY -> hashMapOf<String, Any>(
                "targetCharacterId" to qrData.modelId,
                "power" to castModel.power
            )
            else -> {
                showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код."); return
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mCharacterModel.castSpell(castModel.id, eventData)
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            goBackToSpellbook()
        }
    }

    private fun goBackToSpellbook() {
        findNavController().popBackStack(R.id.spellbookFragment, false)
    }
}
