package org.shadowrunrussia2020.android.character


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_character_overview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R

class CharacterOverviewFragment : Fragment() {

    private lateinit var mModel: CharacterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_character_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
        mModel.getCharacter().observe(this, Observer {
            if (it != null) {
                textViewSpellsCasted.text = it.spellsCasted.toString()
            }
        })

        buttonСastDummySpell.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mModel.postEvent("dummy-spell")
            }
        }

        swipeRefreshLayout.setOnRefreshListener { refreshData() }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.refresh() }
            } catch (e: Exception) {
                Toast.makeText(activity, "Ошибка. ${e.message}", Toast.LENGTH_LONG).show();
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}
