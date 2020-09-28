package org.shadowrunrussia2020.android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history.*
import org.shadowrunrussia2020.android.view.universal_list.GenericListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class GenericListFragment : Fragment() {
    private val args: GenericListFragmentArgs by navArgs()
    private val universalAdapter by lazy { UniversalAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        universalAdapter.clear()

        universalAdapter.appendList(
            args.records.toList().map {
                GenericListItem(it)
            })

        universalAdapter.apply()

        characterHistoryView.adapter = universalAdapter
        characterHistoryView.layoutManager = LinearLayoutManager(requireContext())
    }
}
