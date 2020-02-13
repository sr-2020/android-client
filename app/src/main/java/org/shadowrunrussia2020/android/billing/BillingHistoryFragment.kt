package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_billing_history.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.Transaction

class BillingHistoryFragment : Fragment() {

    private lateinit var mModel: BillingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)

        // See RecyclerView guide for details if needed
        // https://developer.android.com/guide/topics/ui/layout/recyclerview
        transactionHistoryView.setHasFixedSize(true)
        transactionHistoryView.layoutManager = LinearLayoutManager(activity!!)

        val adapter = TransactionsAdapter()
        mModel.getHistory().observe(this,
            Observer { data: List<Transaction>? -> if (data != null) adapter.setData(data) })

        transactionHistoryView.adapter = adapter
    }
}

