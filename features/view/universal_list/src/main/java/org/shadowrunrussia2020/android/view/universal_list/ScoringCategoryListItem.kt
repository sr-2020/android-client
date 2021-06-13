package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.shadowrunrussia2020.android.common.models.ScoringCategory

class ScoringCategoryListItem(
    private val record: ScoringCategory,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.generic_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ScoringCategoryListItemViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as ScoringCategoryListItemViewHolder).bindView(record, onClick, hide)
}

private class ScoringCategoryListItemViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(record: ScoringCategory, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.mainText.text = record.name
        containerView.subText.text = ""
        containerView.time.text = record.value.toString()
        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }
}
