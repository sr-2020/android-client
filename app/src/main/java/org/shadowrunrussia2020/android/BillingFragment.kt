package org.shadowrunrussia2020.android

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_billing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.models.billing.Transaction

class BillingFragment : Fragment() {

    private lateinit var mModel: BillingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)

        mModel.getBalance().observe(this, Observer { balance: Int? -> textViewBalance.text = balance.toString() })

        // See RecyclerView guide for details if needed
        // https://developer.android.com/guide/topics/ui/layout/recyclerview
        transactionHistoryView.setHasFixedSize(true)
        transactionHistoryView.layoutManager = LinearLayoutManager(activity!!)

        val adapter = TransactionsAdapter(mModel)
        mModel.getHistory().observe(this,
            Observer { data: List<Transaction>? -> if (data != null) adapter.setData(data) })

        transactionHistoryView.adapter = adapter

        refreshData()

        swipeRefreshLayout.setOnRefreshListener { refreshData() }

        buttonTransfer.setOnClickListener {
            editTextRecipient.error = null
            editTextAmount.error = null
            val recipient = editTextRecipient.text.toString()
            val amountString = editTextAmount.text.toString()
            if (TextUtils.isEmpty(recipient)) {
                editTextRecipient.error = getString(R.string.error_field_required)
                editTextRecipient.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountString)) {
                editTextAmount.error = getString(R.string.error_field_required)
                editTextAmount.requestFocus()
                return@setOnClickListener
            }
            val amount = Integer.parseInt(amountString)
            if (amount > mModel.getBalance().value ?: 0) {
                editTextAmount.error = getString(R.string.insufficient_money)
                editTextAmount.requestFocus()
                return@setOnClickListener
            }
            val comment = editTextTransferComment.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(Dispatchers.IO) { mModel.transferMoney(Integer.parseInt(recipient), amount, comment) }
                    Toast.makeText(activity, "Перевод осуществлен", Toast.LENGTH_LONG).show();
                    editTextRecipient.text.clear()
                    editTextAmount.text.clear()
                    editTextTransferComment.text.clear()
                    val inputManager: InputMethodManager =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED)
                } catch (e: Exception) {
                    Toast.makeText(activity, "Ошибка. ${e.message}", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.refresh() }
            } catch (e: Exception) {
                Toast.makeText(activity, "Ошибка. ${e.message}", Toast.LENGTH_LONG).show();
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}

