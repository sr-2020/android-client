package org.shadowrunrussia2020.android.magic


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.character.models.Spell
import org.shadowrunrussia2020.android.qr.*

class SpellDetailsFragment : Fragment() {
    private val args: SpellDetailsFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
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

        if (qrData != null) castOnTarget(qrData)

        textSpellName.text = spell.humanReadableName
        textSpellDescription.text = spell.description

        castOnSelf.isEnabled = spell.canTargetSelf
        castOnTarget.isEnabled = spell.canTargetItem || spell.canTargetSingleTarget
        castOnLocation.isEnabled = spell.canTargetLocation

        castOnSelf.setOnClickListener { castOnSelf() }
        castOnTarget.setOnClickListener { chooseTarget() }

        mModel.getCharacter()
            .observe(this, Observer { data: Character? ->
                if (data != null) {
                    seekBarSpellPower.max = data.magic + data.magicPowerBonus
                    val canCast = seekBarSpellPower.max > 0
                    castOnSelf.isEnabled = spell.canTargetSelf && canCast
                    castOnTarget.isEnabled =
                        (spell.canTargetItem || spell.canTargetSingleTarget) && canCast
                    castOnLocation.isEnabled = (spell.canTargetLocation) && canCast
                }
            })
    }

    private fun castOnSelf() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mModel.postEvent(
                        spell.eventType, hashMapOf("power" to seekBarSpellPower.progress)
                    )
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
        }
    }

    private fun chooseTarget() {
        findNavController().navigate(SpellDetailsFragmentDirections.actionChooseSpellTarget())
    }

    private fun castOnTarget(qrData: Data) {
        val eventData = when {
            qrData.type == Type.REWRITABLE -> hashMapOf(
                "qrCode" to qrData.payload.toInt(),
                "power" to seekBarSpellPower.progress
            )
            qrData.type == Type.DIGITAL_SIGNATURE || qrData.type == Type.WOUNDED_BODY -> hashMapOf(
                "targetCharacterId" to qrData.payload.toInt(),
                "power" to seekBarSpellPower.progress
            )
            else -> {
                showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код."); return
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mModel.postEvent(spell.eventType, eventData)
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
        }
    }
}
