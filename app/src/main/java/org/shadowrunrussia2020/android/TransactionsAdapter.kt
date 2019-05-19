package org.shadowrunrussia2020.android

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.shadowrunrussia2020.android.models.billing.Transaction
import java.util.*

class TransactionsAdapter(model: BillingViewModel) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    private var mDataset: List<Transaction> = ArrayList()

    fun setData(newData: List<Transaction>) {
        this.mDataset = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = mDataset[position]
        holder.mUsernameView.text = "${transaction.sin_from} --> ${transaction.sin_to}"
        holder.mCommentView.text = transaction.comment
        // TODO(aeremin) Use PrettyTime
        //holder.mTimeView.text = /*PrettyTime(Locale("ru")).format(*/ transaction.created_at.toString()
        holder.mAmountView.text = /*PrettyTime(Locale("ru")).format(*/ transaction.amount.toString()
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mUsernameView: TextView = itemView.findViewById(R.id.username)
        var mCommentView: TextView = itemView.findViewById(R.id.comment)
        var mAmountView: TextView = itemView.findViewById(R.id.amount)
    }
}
