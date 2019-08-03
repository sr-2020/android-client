package org.shadowrunrussia2020.android.magic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.character.models.Spell
import java.util.*

class SpellsAdapter : RecyclerView.Adapter<SpellsAdapter.ViewHolder>() {
    private var mDataset: List<Spell> = ArrayList()
    private var mModel: CharacterViewModel? = null

    fun setData(newData: List<Spell>) {
        this.mDataset = newData
        notifyDataSetChanged()
    }

    fun setViewModel(m: CharacterViewModel) {
        this.mModel = m
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spell = mDataset[position]
        holder.mAmountView.text = spell.eventType
        holder.mCommentView.text = spell.description
        holder.itemView.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{ mModel?.postEvent(spell.eventType) }
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mAmountView: TextView = itemView.findViewById(R.id.amount)
        var mCommentView: TextView = itemView.findViewById(R.id.comment)
    }
}