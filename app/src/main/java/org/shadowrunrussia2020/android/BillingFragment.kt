package org.shadowrunrussia2020.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.models.billing.Transaction

class BillingFragment : Fragment() {

    private lateinit var mModel: BillingViewModel
    private lateinit var mRecipientField: EditText
    private lateinit var mAmountField: EditText
    private lateinit var mCommentField: EditText

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

        mRecipientField = view.findViewById<EditText>(R.id.editTextRecipient)
        mAmountField = view.findViewById<EditText>(R.id.editTextAmount)
        mCommentField = view.findViewById<EditText>(R.id.editTextTransferComment)

        val button = view.findViewById<Button>(R.id.buttonTransfer)
        button.setOnClickListener {
            mRecipientField.error = null
            mAmountField.error = null
            val recipient = mRecipientField.text.toString()
            val amountString = mAmountField.text.toString()
            if (TextUtils.isEmpty(recipient)) {
                mRecipientField.error = getString(R.string.error_field_required)
                mRecipientField.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountString)) {
                mAmountField.error = getString(R.string.error_field_required)
                mAmountField.requestFocus()
                return@setOnClickListener
            }
            val amount = Integer.parseInt(amountString)
            if (amount > mModel.getBalance().value ?: 0) {
                mAmountField.error = getString(R.string.insufficient_money)
                mAmountField.requestFocus()
                return@setOnClickListener
            }
            val comment = mCommentField.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = mModel.transferMoney(Integer.parseInt(recipient), amount, comment)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Перевод осуществлен", Toast.LENGTH_LONG).show();
                        mRecipientField.text.clear()
                        mAmountField.text.clear()
                        mCommentField.text.clear()
                        val inputManager:InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Ошибка. ${e.message}", Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
    }
}

