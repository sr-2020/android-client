package org.shadowrunrussia2020.android.character


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_character_overview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.qr.showErrorMessage

class CharacterOverviewFragment : Fragment() {

    private lateinit var mModel: CharacterViewModel
    private val database = FirebaseDatabase.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_character_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
        mModel.getCharacter().observe(this, Observer {
            if (it != null) {
                textViewMaxHp.text = it.maxHp.toString()
                textViewMagic.text = it.magic.toString()
                textViewMagicPowerBonus.text = it.magicPowerBonus.toString()
            }
        })

        swipeRefreshLayout.setOnRefreshListener { refreshData() }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.refresh() }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}
