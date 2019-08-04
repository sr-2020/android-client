package org.shadowrunrussia2020.android.magic


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import org.shadowrunrussia2020.android.character.models.Spell
import org.shadowrunrussia2020.android.qr.Data
import org.shadowrunrussia2020.android.qr.QrViewModel
import org.shadowrunrussia2020.android.qr.Type

class SpellDetailsFragment : Fragment() {
    private val args: SpellDetailsFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val mModel by lazy { ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java) }
    private val qrViewModel by lazy { ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_spell_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrData = qrViewModel.data.qrData;
        if (qrData != null) castOnTarget(qrData)

        textSpellName.text = spell.eventType
        textSpellDescription.text = spell.description

        castOnSelf.isEnabled = spell.canTargetSelf
        castOnTarget.isEnabled = spell.canTargetItem || spell.canTargetSingleTarget
        castOnLocation.isEnabled = spell.canTargetLocation

        castOnSelf.setOnClickListener { castOnSelf() }
        castOnTarget.setOnClickListener { chooseTarget() }
    }

    private fun castOnSelf() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.postEvent(spell.eventType) }
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка. ${e.message}", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun chooseTarget() {
        findNavController().navigate(SpellDetailsFragmentDirections.actionChooseSpellTarget())
    }

    private fun castOnTarget(qrData: Data) {
        if (qrData.type == Type.REWRITABLE) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        mModel.postEvent(
                            spell.eventType,
                            hashMapOf("qrCode" to qrData.payload.toInt())
                        )
                    }
                    Toast.makeText(context, "Заклинание применено на цель", Toast.LENGTH_LONG).show();
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка. ${e.message}", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(context, "Ошибка. Неожиданный QR-код.", Toast.LENGTH_LONG).show();
        }
    }
}
