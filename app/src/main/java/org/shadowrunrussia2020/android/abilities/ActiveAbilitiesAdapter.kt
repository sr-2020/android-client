package org.shadowrunrussia2020.android.abilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.ActiveAbility
import java.util.*

class ActiveAbilitiesAdapter : RecyclerView.Adapter<ActiveAbilitiesAdapter.ViewHolder>() {
    private var mDataset: List<ActiveAbility> = ArrayList()

    fun setData(newData: List<ActiveAbility>) {
        this.mDataset = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.generic_recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ability = mDataset[position]
        holder.mMainTextView.text = ability.humanReadableName
        holder.mSubTextView.text = ability.description
        val validUntil = ability.validUntil
        if (validUntil != null) {
            holder.mTimeView.text =
                "Закончится " + PrettyTime(Locale("ru")).format(Date(validUntil))
        }

        holder.itemView.setOnClickListener {
            it.findNavController()
                .navigate(ActiveAbilitiesFragmentDirections.actionSelectActiveAbility(ability))
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mMainTextView: TextView = itemView.findViewById(R.id.mainText)
        var mSubTextView: TextView = itemView.findViewById(R.id.subText)
        var mTimeView: TextView = itemView.findViewById(R.id.time)
    }
}


