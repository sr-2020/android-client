package org.shadowrunrussia2020.android.implants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.shadowrunrussia2020.android.view.universal_list.EthicStateItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class OverviewFragment : Fragment() {

    private val universalAdapter by lazy { UniversalAdapter() }

    private val viewModel by lazy { ViewModelProviders.of(this)[ViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.implants_overview_screen, container, false)

    override fun onResume() {
        super.onResume()

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->

                universalAdapter.clear()

                universalAdapter.appendList(
                    character.ethicState
                        .sortedBy { it.scale }
                        .map { EthicStateItem(it) })

                universalAdapter.apply()
            }
        }

        // ethicTriggersList.adapter = universalAdapter
        // ethicTriggersList.layoutManager = LinearLayoutManager(requireContext())
    }
}

