package org.shadowrunrussia2020.android.abilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.models.PassiveAbility
import java.util.*

class PassiveAbilitiesAdapter : RecyclerView.Adapter<PassiveAbilitiesAdapter.ViewHolder>() {
    private var mDataset: List<PassiveAbility> = ArrayList()

    fun setData(newData: List<PassiveAbility>) {
        this.mDataset = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ability = mDataset[position]
        holder.mMainTextView.text = ability.humanReadableName
        holder.mSubTextView.text = ability.description
        holder.itemView.setOnClickListener {
            it.findNavController()
                .navigate(PassiveAbilitiesFragmentDirections.actionSelectPassiveAbility(ability))
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

