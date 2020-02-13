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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterHistoryAdapter
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.qr.showErrorMessage

class AllPositionsFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(PositionsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_positions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        availableSpellsView.setHasFixedSize(true)
        availableSpellsView.layoutManager = LinearLayoutManager(activity!!)
        val adapter = CharacterHistoryAdapter({})
        mModel.positions().observe(this,
            Observer { data: List<Position>? ->
                if (data != null) adapter.setData(data.map {
                    HistoryRecord(
                        "",
                        it.date.time,
                        it.username,
                        it.location,
                        ""
                    )
                })
            })
        availableSpellsView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener { refreshData() }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.refresh() }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            swipeRefreshLayout?.isRefreshing = false
        }
    }
}
