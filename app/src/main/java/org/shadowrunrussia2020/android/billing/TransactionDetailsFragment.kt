package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_transaction_details.*
import org.shadowrunrussia2020.android.R
import kotlin.math.absoluteValue


class TransactionDetailsFragment : Fragment() {
    private val args: TransactionDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val t = args.transaction
        valueId.text = t.id.toString()
        valueDate.text = t.created_at.toString()
        valueFrom.text = t.sin_from.toString()
        valueTo.text = t.sin_to.toString()
        valueAmount.text = t.amount.absoluteValue.toString()
        valueComment.text = t.comment
    }

}
