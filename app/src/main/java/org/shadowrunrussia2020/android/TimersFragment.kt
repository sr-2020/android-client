package org.shadowrunrussia2020.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_timers.*
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.view.universal_list.TimerListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class TimersFragment : Fragment() {

    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
    }
    private val universalAdapter by lazy { UniversalAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.getCharacter().observe(this,
            Observer { character: Character? ->
                if (character != null) {
                    universalAdapter.clear()

                    universalAdapter.appendList(
                        character.timers
                            .sortedBy { it.miliseconds }
                            .map { TimerListItem(character.timestamp, it) })

                    universalAdapter.apply()
                }
            })

        characterTimersView.adapter = universalAdapter
        characterTimersView.layoutManager = LinearLayoutManager(requireContext())
    }
}
