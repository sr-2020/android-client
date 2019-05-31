package org.shadowrunrussia2020.android.qr


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.shadowrunrussia2020.android.R


class BillScannedFragment : Fragment() {

    private val args: BillScannedFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bill_scanned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(
            activity!!,
            "Пытаемся перевести %d нью-йен на аккаунт %d, комментарий: %s".format(
                args.transfer.amount,
                args.transfer.sin_to,
                args.transfer.comment
            ),
            Toast.LENGTH_LONG
        ).show()
        findNavController().navigate(R.id.action_global_back_to_main)
    }
}
