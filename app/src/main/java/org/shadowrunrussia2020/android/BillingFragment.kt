package org.shadowrunrussia2020.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.shadowrunrussia2020.android.models.billing.Transaction

class BillingFragment : Fragment() {

    private lateinit var mModel: BillingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)

        val textViewBalance = view.findViewById<TextView>(R.id.textViewBalance)
        mModel.getBalance().observe(this, Observer { balance: Int? -> textViewBalance.text = balance.toString() })

        // See RecyclerView guide for details if needed
        // https://developer.android.com/guide/topics/ui/layout/recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.transactionHistoryView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)

        val adapter = TransactionsAdapter(mModel)
        mModel.getHistory().observe(this,
            Observer { data: List<Transaction>? -> if (data != null) adapter.setData(data) })

        recyclerView.adapter = adapter
    }
}
