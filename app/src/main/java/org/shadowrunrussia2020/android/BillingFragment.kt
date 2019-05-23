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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.shadowrunrussia2020.android.models.billing.Transaction
import kotlinx.coroutines.*

data class ErrorMessage(val message: String)
data class Error(val error: ErrorMessage)

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

        val button = view.findViewById<Button>(R.id.buttonTransfer)
        button.setOnClickListener {
            val receiver = Integer.parseInt(view.findViewById<EditText>(R.id.editTextRecipient).text.toString())
            val amount = Integer.parseInt(view.findViewById<EditText>(R.id.editTextAmount).text.toString())
            val comment = view.findViewById<EditText>(R.id.editTextTransferComment).text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val result = mModel.transferMoney(receiver, amount, comment)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        Toast.makeText(activity, "Перевод осуществлен", Toast.LENGTH_LONG).show();
                        // TODO(aeremin): Clean input fields, un-focus them
                    } else {
                        val m = Gson().fromJson(result.errorBody()!!.string(), Error::class.java).error.message
                        Toast.makeText(activity, "Ошибка. Некорректный перевод: ${m}", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
