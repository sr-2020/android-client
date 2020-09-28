package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import java.util.*

class GenericListItem(
    // TODO(aeremin) Use dedicated data class
    private val record: HistoryRecord,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.generic_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = GenericHistoryViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as GenericHistoryViewHolder).bindView(record, onClick, hide)
}

private class GenericHistoryViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(record: HistoryRecord, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.mainText.text = record.title
        containerView.subText.text = record.shortText
        containerView.time.text = PrettyTime(Locale("ru")).format(Date(record.timestamp))

        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }
}
