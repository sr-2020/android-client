package org.shadowrunrussia2020.android.magic.cast


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_spell_cast.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.common.CommonHostViewModel
import org.shadowrunrussia2020.android.common.di.MainActivityScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.utils.Data
import org.shadowrunrussia2020.android.common.utils.Type
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.magic.R

class SpellCastFragment : Fragment() {
//    private val args: SpellDetailsFragmentArgs by navArgs()
//    private val spell: Spell by lazy { args.spell }
//    private val mCharacterModel by lazy {
//        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
//    }

    companion object{
        private const val EXTRA_SPELL_ID = "org.shadowrunrussia2020.android.magic.cast.spell"
        fun createBundle(spellId:String) = Bundle().apply {
            putString(EXTRA_SPELL_ID, spellId)
        }
    }
    private val router by lazy { (activity as MainActivityScope).router }
    private val spellId by lazy { arguments?.getString(EXTRA_SPELL_ID) ?: throw IllegalStateException("no spellId") }

    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SpellCastViewModel::class.java)
    }

    private val commonHostViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CommonHostViewModel::class.java)
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

        viewModel.spellId = spellId
        val spell = viewModel.spell


//        val qrViewModel = ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java)
//        val qrData = qrViewModel.data.qrData;
//        qrViewModel.data = QrDataOrError(null, false)
//
//        if (qrData != null) {
//            seekBarSpellPower.progress = viewModel.power
//            castOnTarget(qrData)
//        }

        spell.observe({this.lifecycle}){ spell ->
            spell?:return@observe

            textAbilityName.text = spell.humanReadableName
            textAbilityDescription.text = spell.description

            updateEnableness(true)

            castSpell.setOnClickListener {
                if (spell.hasTarget) chooseTarget() else castOnSelf()
            }
        }


        seekBarSpellPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateEnableness(progress > 0)
                viewModel.power = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.character
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
                    viewModel.castSpell(
                        spellId, hashMapOf("power" to seekBarSpellPower.progress)
                    )
                }

                response?.tableResponse ?.let { tableResponse->
                    router.showSpellResult( tableResponse.map {
                        HistoryRecord(
                            "", it.timestamp,
                            "${it.spellName}, мощь: ${it.power}, откат: ${it.magicFeedback}",
                            it.casterAura, ""
                        )
                    }.toTypedArray())
                } ?: findNavController().popBackStack()

            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                findNavController().popBackStack()
            }
        }
    }

    private fun chooseTarget() {TODO()}
//    {
//        findNavController().navigate(SpellDetailsFragmentDirections.actionChooseSpellTarget())
//    }

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
                    viewModel.castSpell(spellId, eventData)
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            findNavController().popBackStack()
        }
    }

}
