package org.shadowrunrussia2020.android.qr


import android.content.Intent
import android.net.Uri
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
import org.shadowrunrussia2020.android.common.models.FullQrData
import org.shadowrunrussia2020.android.common.models.QrType
import org.shadowrunrussia2020.android.common.models.Transfer
import org.shadowrunrussia2020.android.common.utils.russianQrType
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.startQrScan


class ScanAnyQrFragment : Fragment() {
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
                QrType.PAYMENT_REQUEST_WITH_PRICE -> {
                    val parts = qrData.modelId.split(',', limit = 3)
                    askForTransfer(
                        Transfer(
                            sin_to = parts[0].toInt(),
                            amount = parts[1].toInt(),
                            comment = parts[2]
                        )
                    )
                }
                QrType.pill, QrType.food -> showConsumableQrInfo(qrData)
                QrType.ability -> consume(qrData.modelId)
                QrType.WOUNDED_BODY -> {
                    findNavController().navigate(
                        ScanAnyQrFragmentDirections.actionInteractWithBody(qrData.modelId)
                    )
                }
                QrType.SHOP_BILL -> {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://rc-web.evarun.ru/shop/offer/${qrData.modelId}"))
                    startActivity(browserIntent)
                }
                else -> showQrInfo(qrData)
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
                    findNavController().popBackStack(R.id.scanAnyQrFragment, true)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().popBackStack()
            }
            .create().show()
    }

    private fun showQrInfo(qr: FullQrData) {
        AlertDialog.Builder(requireActivity())
            .setTitle(russianQrType(qr.type) + ": " + qr.name)
            .setMessage(qr.description)
            .setPositiveButton(R.string.ok) { _, _ ->
                findNavController().popBackStack()
            }
            .create().show()
    }

    private fun showConsumableQrInfo(qr: FullQrData) {
        AlertDialog.Builder(requireActivity())
            .setTitle(russianQrType(qr.type))
            .setMessage(qr.description)
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().popBackStack()
            }
            .setPositiveButton(getString(R.string.consume)) { _, _ ->
                CoroutineScope(Dispatchers.Main).launch { consume(qr.modelId) }
            }
            .create().show()
    }


    private suspend fun consume(qrId: String) {
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
