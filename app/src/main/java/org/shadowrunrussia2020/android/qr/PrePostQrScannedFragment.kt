package org.shadowrunrussia2020.android.qr


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
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.billing.BillingViewModel
import org.shadowrunrussia2020.android.billing.models.Transfer
import org.shadowrunrussia2020.android.character.CharacterViewModel


class PrePostQrScannedFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pre_post_qr_scanned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLoader.visibility = View.INVISIBLE

        val vmData = viewModel.data
        viewModel.data = QrDataOrError(qrData = null, error = false)

        if (vmData.error) {
            findNavController().popBackStack()
            return
        }

        val qrData = vmData.qrData
        if (qrData == null) {
            findNavController().navigate(PrePostQrScannedFragmentDirections.actionStartScan())
            return
        }

        when (qrData.type) {
            Type.PAYMENT_REQUEST_WITH_PRICE -> {
                try {
                    val parts = qrData.payload.split(',', limit = 3)
                    askForTransfer(Transfer(sin_to = parts[0].toInt(), amount = parts[1].toInt(), comment = parts[2]))
                    return
                } catch (e: Exception) {
                    showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код.")
                }
            }
            Type.REWRITABLE -> {
                try {
                    consume(qrData.payload.toInt())
                    return
                } catch (e: Exception) {
                    showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код.")
                }
            }
            Type.WOUNDED_BODY -> {
                try {
                    findNavController().navigate(
                        PrePostQrScannedFragmentDirections.actionInteractWithBody(
                            qrData.payload.toInt()
                        )
                    )
                    return
                } catch (e: Exception) {
                    showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код.")
                }
            }
            else -> {
                showInfoMessage(requireContext(), "Содержимое QR-кода: ${qrData.type}, ${qrData.payload}")
                findNavController().popBackStack()
            }
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
                    val m = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)
                    progressLoader.visibility = View.VISIBLE
                    withContext(CoroutineScope(Dispatchers.IO).coroutineContext)
                    { m.transferMoney(receiver = t.sin_to, amount = t.amount, comment = t.comment) }
                    findNavController().popBackStack(R.id.prePostQrScannedFragment, true)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().popBackStack()
            }
            .create().show()
    }

    private fun consume(qrId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val m = ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
            progressLoader.visibility = View.VISIBLE
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext)
                { m.postEvent("scanQr", hashMapOf("qrCode" to qrId)) }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), e.message?: "Неожиданная ошибка сервера")
            }
            findNavController().popBackStack()
        }
    }
}
