package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_active_abilities.*
import kotlinx.android.synthetic.main.fragment_spellbook.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.character.models.Character

class ActiveAbilitiesFragment : Fragment() {
    private val mModel by lazy { ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_abilities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        availableAbilitiesView.setHasFixedSize(true)
        availableAbilitiesView.layoutManager = LinearLayoutManager(activity!!)
        val adapter = ActiveAbilitiesAdapter()
        mModel.getCharacter().observe(this,
            Observer { data: Character? -> if (data != null) adapter.setData(data.activeAbilities) })
        availableAbilitiesView.adapter = adapter
    }
}
