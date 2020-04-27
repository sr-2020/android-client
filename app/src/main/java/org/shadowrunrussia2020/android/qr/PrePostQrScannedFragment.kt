package org.shadowrunrussia2020.android.qr


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.pre_post_qr_scanned.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.billing.BillingViewModel
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.Transfer
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.common.utils.showInfoMessage
import org.shadowrunrussia2020.android.model.qr.FullQrData
import org.shadowrunrussia2020.android.model.qr.Type
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.startQrScan


class PrePostQrScannedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pre_post_qr_scanned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLoader.visibility = View.INVISIBLE
        scanQr()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, onQrScanned = {
            onQr(it)
        }, onScanCancelled = {
            findNavController().popBackStack()
        })
    }

    private suspend fun onQr(qrData: FullQrData) {
        try {
            when (qrData.type) {
                Type.PAYMENT_REQUEST_WITH_PRICE -> {
                    val parts = qrData.modelId.split(',', limit = 3)
                    askForTransfer(
                        Transfer(
                            sin_to = parts[0].toInt(),
                            amount = parts[1].toInt(),
                            comment = parts[2]
                        )
                    )
                }
                Type.pill, Type.food, Type.ability -> consume(qrData.modelId.toInt())
                Type.WOUNDED_BODY -> {
                    findNavController().navigate(
                        PrePostQrScannedFragmentDirections.actionInteractWithBody(
                            qrData.modelId.toInt()
                        )
                    )
                }
                else -> {
                    showInfoMessage(
                        requireContext(),
                        "Содержимое QR-кода: ${qrData.type}, ${qrData.modelId}"
                    )
                    findNavController().popBackStack()
                }
            }
        } catch (e: Exception) {
            showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код.")
        }
    }

    private fun askForTransfer(t: Transfer) {
        AlertDialog.Builder(activity!!)
            .setMessage(
                "Запрос о переводе %d нью-йен на аккаунт %d. Комментарий: %s. Подтверждаете перевод?".format(
                    t.amount,
                    t.sin_to,
                    t.comment
                )
            )
            .setPositiveButton(R.string.ok) { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    progressLoader.visibility = View.VISIBLE
                    ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)
                        .transferMoney(receiver = t.sin_to, amount = t.amount, comment = t.comment)
                    findNavController().popBackStack(R.id.prePostQrScannedFragment, true)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().popBackStack()
            }
            .create().show()
    }

    private suspend fun consume(qrId: Int) {
        val m = ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
        progressLoader.visibility = View.VISIBLE
        try {
            m.postEvent("scanQr", hashMapOf("qrCode" to qrId))
        } catch (e: Exception) {
            showErrorMessage(requireContext(), e.message ?: "Неожиданная ошибка сервера")
        }
        findNavController().popBackStack()
    }

    private fun scanQr() {
        startQrScan(this, "Сканирование QR-кода.")
    }
}
