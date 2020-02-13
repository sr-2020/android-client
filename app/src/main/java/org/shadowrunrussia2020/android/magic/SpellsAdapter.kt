package org.shadowrunrussia2020.android.magic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.Spell
import java.util.*

class SpellsAdapter : RecyclerView.Adapter<SpellsAdapter.ViewHolder>() {
    private var mDataset: List<Spell> = ArrayList()

    fun setData(newData: List<Spell>) {
        this.mDataset = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spell = mDataset[position]
        holder.mMainTextView.text = spell.humanReadableName
        holder.mSubTextView.text = spell.description
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(SpellbookFragmentDirections.actionSelectSpell(spell))
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mMainTextView: TextView = itemView.findViewById(R.id.mainText)
        var mSubTextView: TextView = itemView.findViewById(R.id.subText)
    }
}