package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.shadowrunrussia2020.android.common.models.ActiveAbility
import org.shadowrunrussia2020.android.common.utils.Data
import org.shadowrunrussia2020.android.common.utils.Type
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.qr.*

class ActiveAbilityDetailsFragment : Fragment() {
    private val args: ActiveAbilityDetailsFragmentArgs by navArgs()
    private val ability: ActiveAbility by lazy { args.ability }
    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_ability_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrViewModel = ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java)
        val qrData = qrViewModel.data.qrData;
        qrViewModel.data = QrDataOrError(null, false)

        if (qrData != null) castOnTarget(qrData)

        textAbilityName.text = ability.humanReadableName
        textAbilityDescription.text = ability.description

        castOnSelf.isEnabled = ability.canTargetSelf
        castOnTarget.isEnabled = ability.canTargetSingleTarget

        castOnSelf.setOnClickListener { castOnSelf() }
        castOnTarget.setOnClickListener { chooseTarget() }
    }

    private fun castOnSelf() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mModel.postEvent(ability.eventType)
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            findNavController().popBackStack()
        }
    }

    private fun chooseTarget() {
        findNavController().navigate(ActiveAbilityDetailsFragmentDirections.actionChooseAbilityTarget())
    }

    private fun castOnTarget(qrData: Data) {
        val eventData: HashMap<String, Any> = when {
            qrData.type == Type.DIGITAL_SIGNATURE || qrData.type == Type.WOUNDED_BODY -> hashMapOf(
                "targetCharacterId" to qrData.payload.toInt()
            )
            else -> {
                showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код."); return
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mModel.postEvent(ability.eventType, eventData)
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            findNavController().popBackStack()
        }
    }
}
