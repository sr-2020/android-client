package org.shadowrunrussia2020.android.billing

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_billing_overview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.common.utils.showSuccessMessage

class BillingOverviewFragment : Fragment() {

    private lateinit var mModel: BillingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)
        val accountOverview = mModel.getAccountOverview()
        accountOverview.observe(this, Observer {
            if (it != null) {
                textViewBalance.text = it.balance.toString()
                textViewSin.text = it.sin.toString()
            }
        })

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
            if (amount > accountOverview.value?.balance ?:  0) {
                editTextAmount.error = getString(R.string.insufficient_money)
                editTextAmount.requestFocus()
                return@setOnClickListener
            }
            val comment = editTextTransferComment.text.toString()
            buttonTransfer.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(Dispatchers.IO) { mModel.transferMoney(Integer.parseInt(recipient), amount, comment) }
                    showSuccessMessage(requireContext(), "Перевод осуществлен")
                    editTextRecipient.text!!.clear()
                    editTextAmount.text!!.clear()
                    editTextTransferComment.text!!.clear()
                    val inputManager: InputMethodManager =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED)
                } catch (e: Exception) {
                    showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                } finally {
                    buttonTransfer.isEnabled = true
                }
            }

        }
    }
}

