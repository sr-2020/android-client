package org.shadowrunrussia2020.android.character

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.HistoryFragmentDirections
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.models.HistoryRecord
import java.util.*

class CharacterHistoryAdapter : RecyclerView.Adapter<CharacterHistoryAdapter.ViewHolder>() {
    private var mDataset: List<HistoryRecord> = ArrayList()

    fun setData(newData: List<HistoryRecord>) {
        this.mDataset = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = mDataset[position]
        holder.mMainTextView.text = record.title
        holder.mSubTextView.text = record.shortText
        holder.mTimeTextView.text = PrettyTime(Locale("ru")).format(Date(record.timestamp))
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(HistoryFragmentDirections.actionSelectHistoryRecord(record))
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mMainTextView: TextView = itemView.findViewById(R.id.mainText)
        var mSubTextView: TextView = itemView.findViewById(R.id.subText)
        var mTimeTextView: TextView = itemView.findViewById(R.id.time)
    }
}