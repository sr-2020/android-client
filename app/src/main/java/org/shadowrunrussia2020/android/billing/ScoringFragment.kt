package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_billing_scoring.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.ScoringInfo
import org.shadowrunrussia2020.android.view.universal_list.ScoringCategoryListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class BillingScoringFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)
    }
    private val universalAdapterRelative by lazy { UniversalAdapter() }
    private val universalAdapterFix by lazy { UniversalAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing_scoring, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.getScoringInfo().observe(this,
            Observer { info: ScoringInfo? ->
                if (info != null) {
                    textViewScoringFixCoefficient.text = info.currentFix.toString()
                    textViewScoringRelativeCoefficient.text = info.currentRelative.toString()

                    universalAdapterFix.clear()

                    universalAdapterFix.appendList(
                        info.fixCategories.map {
                            ScoringCategoryListItem(it)
                        })

                    universalAdapterFix.apply()

                    universalAdapterRelative.clear()

                    universalAdapterRelative.appendList(
                        info.relativeCategories.map {
                            ScoringCategoryListItem(it)
                        })

                    universalAdapterRelative.apply()
                }
            })

        scoringFixCategories.adapter = universalAdapterFix
        scoringRelativeCategories.adapter = universalAdapterRelative

        scoringFixCategories.layoutManager = LinearLayoutManager(requireContext())
        scoringRelativeCategories.layoutManager = LinearLayoutManager(requireContext())
    }
}

