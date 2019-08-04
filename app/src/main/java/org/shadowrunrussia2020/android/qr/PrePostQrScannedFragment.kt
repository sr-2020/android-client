package org.shadowrunrussia2020.android.qr


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


class PrePostQrScannedFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pre_post_qr_scanned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLoader.visibility = View.INVISIBLE
        if (viewModel.data.error) {
            findNavController().navigate(PrePostQrScannedFragmentDirections.actionGlobalBackToMain())
            return
        }

        val qrData = viewModel.data.qrData
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
                    Toast.makeText(activity!!, "Неподдерживаемый QR-код", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Toast.makeText(
                    activity!!,
                    "Содержимое QR-кода: ${qrData.type}, ${qrData.payload}",
                    Toast.LENGTH_LONG
                ).show()
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
                    findNavController().navigate(R.id.action_global_back_to_main)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().navigate(R.id.action_global_back_to_main)
            }
            .create().show()
    }
}
