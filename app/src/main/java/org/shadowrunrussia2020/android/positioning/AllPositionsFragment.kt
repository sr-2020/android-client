package org.shadowrunrussia2020.android.positioning


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_spellbook.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.view.universal_list.GenericListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class AllPositionsFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(PositionsViewModel::class.java)
    }
    private val universalAdapter by lazy { UniversalAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_positions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.positions().observe(this,
            Observer { positions: List<Position>? ->
                if (positions != null) {
                    universalAdapter.clear()

                    universalAdapter.appendList(
                        positions.map {
                            GenericListItem(HistoryRecord(
                                "",
                                it.date.time,
                                it.username,
                                it.location,
                                ""
                            ))
                        })

                    universalAdapter.apply()
                }
            })

        availableSpellsView.adapter = universalAdapter
        availableSpellsView.layoutManager = LinearLayoutManager(requireContext())
    }
}
