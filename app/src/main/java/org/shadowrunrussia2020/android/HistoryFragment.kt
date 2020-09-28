package org.shadowrunrussia2020.android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history.*
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.view.universal_list.GenericListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class HistoryFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
    }
    private val universalAdapter by lazy { UniversalAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.getHistory().observe(this,
            Observer { history: List<HistoryRecord>? ->
                if (history != null) {
                    universalAdapter.clear()

                    universalAdapter.appendList(
                        history.map {
                            GenericListItem(it) {
                                findNavController().navigate(
                                    HistoryFragmentDirections.actionSelectHistoryRecord(
                                        it
                                    )
                                )
                            }
                        })

                    universalAdapter.apply()
                }
            })

        characterHistoryView.adapter = universalAdapter
        characterHistoryView.layoutManager = LinearLayoutManager(requireContext())
    }
}
