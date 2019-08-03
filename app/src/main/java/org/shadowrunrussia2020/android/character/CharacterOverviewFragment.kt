package org.shadowrunrussia2020.android.character


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_character_overview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.magic.SpellsAdapter

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
                textViewSpellsCasted.text = it.spellsCasted.toString()
            }
        })

        buttonCastDummySpell.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mModel.postEvent("dummy-spell")
            }
        }

        database.getReference("spellsCasted").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val p = dataSnapshot.getValue(Int::class.java) ?: return
                textTest1.text = p.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException())
            }
        })

        buttonTest1.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                database.getReference("spellsCasted").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val p = mutableData.getValue(Int::class.java)
                            ?: return Transaction.success(mutableData)
                        mutableData.value = p + 1
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {}
                })
            }
        }

        val docRef = firestore.collection("characters").document("8")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("Firestore", "Current data: ${snapshot.data}")
                textTest2.text = snapshot.data!!["spellsCasted"].toString()
            } else {
                Log.d("Firestore", "Current data: null")
            }
        }

        buttonTest2.setOnClickListener {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val newPopulation = snapshot.getLong("spellsCasted")!! + 1
                transaction.update(docRef, "spellsCasted", newPopulation)
            }
        }

        swipeRefreshLayout.setOnRefreshListener { refreshData() }


        // See RecyclerView guide for details if needed
        // https://developer.android.com/guide/topics/ui/layout/recyclerview
        availableSpellsView.setHasFixedSize(true)
        availableSpellsView.layoutManager = LinearLayoutManager(activity!!)

        val adapter = SpellsAdapter()
        mModel.getCharacter().observe(this,
            Observer { data: Character? -> if (data != null) adapter.setData(data.spells) })

        adapter.setViewModel(mModel)

        availableSpellsView.adapter = adapter
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
