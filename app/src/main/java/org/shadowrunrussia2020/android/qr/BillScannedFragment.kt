package org.shadowrunrussia2020.android.qr


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.transition.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_bill_scanned.*
import kotlinx.android.synthetic.main.recycler_view_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.billing.BillingViewModel


class BillScannedFragment : Fragment() {

    private val args: BillScannedFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bill_scanned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLoader.visibility = View.INVISIBLE
        val t = args.transfer
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
