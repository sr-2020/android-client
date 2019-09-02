package org.shadowrunrussia2020.android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history.*
import org.shadowrunrussia2020.android.character.CharacterHistoryAdapter

class GenericListFragment : Fragment() {
    private val args: GenericListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        characterHistoryView.setHasFixedSize(true)
        characterHistoryView.layoutManager = LinearLayoutManager(activity!!)
        val adapter = CharacterHistoryAdapter {}
        adapter.setData(args.records.toList())
        characterHistoryView.adapter = adapter
    }
}
