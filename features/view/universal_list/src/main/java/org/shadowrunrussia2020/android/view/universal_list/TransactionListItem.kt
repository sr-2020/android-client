package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.transaction_item.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.Transaction
import java.util.*

class TransactionListItem(
    private val transaction: Transaction,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.billing_history_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = TransactionViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as TransactionViewHolder).bindView(transaction, onClick, hide)
}

private class TransactionViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false))

    fun bindView(transaction: Transaction, onClick: (() -> Unit)?, hide: Boolean) {

        if (transaction.transferType == "Incoming") {
            containerView.direction.setImageResource(R.drawable.ic_transfer_to)
            containerView.comment.text = commentInBrackets(transaction)
            containerView.amount.text = transaction.amount.toString()
        } else {
            containerView.direction.setImageResource(R.drawable.ic_transfer_from)
            containerView.comment.text = commentInBrackets(transaction)
            containerView.amount.text = (-transaction.amount).toString()
        }
        containerView.time.text  = PrettyTime(Locale("ru")).format(transaction.operationTime)

        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }

    private fun commentInBrackets(t: Transaction): String {
        val comment = t.comment
        if (comment == null || comment.isEmpty()) return ""
        return "($comment)"
    }
}
