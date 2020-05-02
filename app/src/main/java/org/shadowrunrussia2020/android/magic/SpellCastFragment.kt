package org.shadowrunrussia2020.android.magic


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_spell_cast.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.FullQrData
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.QrType
import org.shadowrunrussia2020.android.common.models.Spell
import org.shadowrunrussia2020.android.common.utils.MainThreadSchedulers
import org.shadowrunrussia2020.android.common.utils.plusAssign
import org.shadowrunrussia2020.android.common.utils.russianQrType
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.startQrScan
import java.util.concurrent.TimeUnit

class SpellCastFragment : Fragment() {
    private val args: SpellCastFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val power: Int by lazy { args.power }
    private val disposer = CompositeDisposable()

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

        mCharacterModel.getCharacter().observe(this, Observer{character ->
            if (character != null) {
                addRitualMember.isVisible = character.passiveAbilities.any { it.id.contains("ritual-magic") }
            }
        })

        updateButtonLabels()

        castSpell.setOnClickListener {
            setButtonsEnabled(false)

            val delay = (10..15).random()
            disposer += Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(MainThreadSchedulers.androidUiScheduler)
                .subscribe { sinceStart ->
                    if (delay > sinceStart) {
                        castSpell.text = "Подготовка. Осталось ${delay-sinceStart} секунд."
                    } else {
                        disposer.clear()
                        if (spell.hasTarget) {
                            castModel.qrReason = QrReason.TARGET
                            startQrScan(this, "Выбор цели заклинания.")
                        } else cast()
                    }
                }
        }

        addReagent.setOnClickListener {
            castModel.qrReason = QrReason.REAGENT
            startQrScan(this, "Добавление реагента.")
        }

        addRitualMember.setOnClickListener {
            castModel.qrReason = QrReason.RITUAL_MEMBER
            startQrScan(this, "Добавление участника ритуала.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposer.clear()
    }

    private fun setButtonsEnabled(on: Boolean) {
        castSpell.isEnabled = on
        addRitualMember.isEnabled = on
        addReagent.isEnabled = on
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, {
            when (castModel.qrReason) {
                QrReason.REAGENT -> onAddReagent(it)
                QrReason.RITUAL_MEMBER -> onAddRitualMember(it)
                QrReason.TARGET -> onTargetPicked(it)
                null -> {}
            }
            updateButtonLabels()
            castModel.qrReason = null
        }, {
          if (castModel.qrReason == QrReason.TARGET) {
              // User was stupid enough to cancel picking the target after the delay...
              // Let's force them wait once again!
              setButtonsEnabled(true)
              castSpell.text = "Приступить"
          }
        })
    }

    private fun onAddReagent(fullQrData: FullQrData) {
        if (fullQrData.type != QrType.reagent) {
            showErrorMessage(requireContext(), "Неожиданный QR-код: ${russianQrType(fullQrData.type)}. Подходящие: Реагент.");
            return
        }

        castModel.reagentIds.add(fullQrData.modelId)
    }

    private fun onAddRitualMember(fullQrData: FullQrData) {
        if (fullQrData.type != QrType.HEALTHY_BODY && fullQrData.type != QrType.WOUNDED_BODY) {
            showErrorMessage(requireContext(), "Неожиданный QR-код: ${russianQrType(fullQrData.type)}. Подходящие: Живое физическое тело.");
            return
        }

        castModel.ritualMembersIds.add(fullQrData.modelId)
    }

    private fun onTargetPicked(fullQrData: FullQrData) {
        if (fullQrData.type != QrType.HEALTHY_BODY && fullQrData.type != QrType.WOUNDED_BODY) {
            showErrorMessage(requireContext(), "Неожиданный QR-код: ${russianQrType(fullQrData.type)}. Подходящие: Живое физическое тело.");
            return
        }

        castModel.targetCharacterId = fullQrData.modelId
        cast()
    }

    private fun cast() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val data: HashMap<String, Any> = hashMapOf(
                    "power" to castModel.power,
                    "ritualMembersIds" to castModel.ritualMembersIds.toList(),
                    "reagentIds" to castModel.reagentIds.toList()
                )
                if (castModel.targetCharacterId != null) {
                    data["targetCharacterId"] = castModel.targetCharacterId!!
                }
                val response = mCharacterModel.castSpell(castModel.id, data)

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

    private fun goBackToSpellbook() {
        findNavController().popBackStack(R.id.spellbookFragment, false)
    }

    private fun updateButtonLabels() {
        addReagent.text = "Реагент (${castModel.reagentIds.size})"
        addRitualMember.text = "Участник ритуала (${castModel.ritualMembersIds.size})"
    }
}
