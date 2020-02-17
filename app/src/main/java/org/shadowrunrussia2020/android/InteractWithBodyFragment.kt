package org.shadowrunrussia2020.android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_interact_with_body.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.utils.Data
import org.shadowrunrussia2020.android.common.utils.Type
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.qr.*

class InteractWithBodyFragment : Fragment() {
    private val args: InteractWithBodyFragmentArgs by navArgs()
    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interact_with_body, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrViewModel = ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java)
        val qrData = qrViewModel.data.qrData;
        qrViewModel.data = QrDataOrError(null, false)
        if (qrData != null) injectMedication(qrData)

        textViewTitle.text = "Это тело #${args.targetId}"

        buttonMedication.setOnClickListener { chooseMedication() }
        buttonKill.setOnClickListener { finishToClinicalDeath() }
    }

    private fun chooseMedication() {
        findNavController().navigate(InteractWithBodyFragmentDirections.actionChooseMedication())
    }

    private fun injectMedication(data: Data) {
        if (data.type != Type.REWRITABLE) {
            showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext)
                {
                    mModel.postEvent(
                        "scanQr",
                        hashMapOf(
                            "qrCode" to data.payload.toInt(),
                            "targetCharacterId" to args.targetId
                        )
                    )
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), e.message ?: "Неожиданная ошибка сервера")
            }
        }
    }

    private fun finishToClinicalDeath() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    mModel.postEvent(
                        "clinicalDeathOnTarget",
                        hashMapOf("targetCharacterId" to args.targetId)
                    )
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
        }
    }
}
