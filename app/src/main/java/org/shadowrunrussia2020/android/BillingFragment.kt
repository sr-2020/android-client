package org.shadowrunrussia2020.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.shadowrunrussia2020.android.models.billing.Transaction

class BillingFragment : Fragment() {

    private lateinit var mModel: BillingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)

        val textViewBalance = view.findViewById<TextView>(R.id.textViewBalance)
        mModel.getBalance().observe(this, Observer { balance: Int? -> textViewBalance.text =
            "Баланс: ${balance.toString()}"
        })
        val textViewHistory = view.findViewById<TextView>(R.id.textViewHistory)
        mModel.getHistory().observe(this, Observer { h: List<Transaction>? -> textViewHistory.text =
            "Всего операций: ${h?.size.toString()}"
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }
}
