package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_billing_rents.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.Rent
import org.shadowrunrussia2020.android.view.universal_list.RentListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class BillingRentsFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)
    }
    private val universalAdapter by lazy { UniversalAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing_rents, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.getRents().observe(this,
            Observer { rents: List<Rent>? ->
                if (rents != null) {
                    universalAdapter.clear()

                    universalAdapter.appendList(
                        rents.map {
                                RentListItem(it) {
                                    findNavController().navigate(
                                        BillingFragmentDirections.actionRentDetails(
                                            it
                                        )
                                    )
                                }
                            })

                    universalAdapter.apply()
                }
            })

        rentsView.adapter = universalAdapter
        rentsView.layoutManager = LinearLayoutManager(requireContext())
    }
}

