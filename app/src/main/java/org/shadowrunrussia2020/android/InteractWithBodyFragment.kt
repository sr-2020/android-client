package org.shadowrunrussia2020.android


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_interact_with_body.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.Data
import org.shadowrunrussia2020.android.model.qr.Type
import org.shadowrunrussia2020.android.model.qr.maybeProcessActivityResult
import org.shadowrunrussia2020.android.model.qr.startQrScan

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

        textViewTitle.text = "Это тело #${args.targetId}"

        buttonMedication.setOnClickListener { chooseMedication() }
        buttonKill.setOnClickListener { finishToClinicalDeath() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val qrData = maybeProcessActivityResult(activity!!, requestCode, resultCode, data)
        if (qrData != null) {
            // TODO(aeremin) Retrieve FullQrData
            injectMedication(qrData)
        }
    }

    private fun chooseMedication() {
        startQrScan(this,"Выбор препарата.")
    }

    private fun injectMedication(data: Data) {
        if (data.type != Type.REWRITABLE) {
            showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                mModel.postEvent(
                    "scanQr",
                    hashMapOf(
                        "qrCode" to data.payload.toInt(),
                        "targetCharacterId" to args.targetId
                    )
                )
            } catch (e: Exception) {
                showErrorMessage(requireContext(), e.message ?: "Неожиданная ошибка сервера")
            }
        }
    }

    private fun finishToClinicalDeath() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
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
