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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.character.CharacterHistoryAdapter
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.qr.showErrorMessage

class HistoryFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
    }

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
        val adapter = CharacterHistoryAdapter {
            findNavController().navigate(HistoryFragmentDirections.actionSelectHistoryRecord(it))
        }
        mModel.getHistory().observe(this,
            Observer { data: List<HistoryRecord>? -> if (data != null) adapter.setData(data) })
        characterHistoryView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener { refreshData() }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.refresh() }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "${e.message}")
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}
