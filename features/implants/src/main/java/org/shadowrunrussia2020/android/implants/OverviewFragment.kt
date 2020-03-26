package org.shadowrunrussia2020.android.implants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.implants_overview_screen.*
import org.shadowrunrussia2020.android.view.universal_list.ImplantItem
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
                    character.implants
                        .sortedBy { it.slot }
                        .map { ImplantItem(it) {
                            findNavController().navigate(OverviewFragmentDirections.actionSelectImplant(it))
                        } })

                universalAdapter.apply()
            }
        }

        implantsList.adapter = universalAdapter
        implantsList.layoutManager = LinearLayoutManager(requireContext())
    }
}

