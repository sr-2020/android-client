package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_billing_passport.*
import org.shadowrunrussia2020.android.R

class BillingPassportFragment : Fragment() {

    private lateinit var mModel: BillingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing_passport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)
        val accountOverview = mModel.getAccountOverview()
        accountOverview.observe(this, Observer {
            if (it != null) {
                textViewName.text = it.personName
                textViewMetatype.text = it.metatype
                textViewCitizenship.text = it.citizenship
                textViewStockManager.text = it.pledgee
                textViewVisa.text = it.viza
                textViewInsurance.text = it.insurance
                textViewLicenses.text = it.licenses.joinToString()
            }
        })
    }
}

