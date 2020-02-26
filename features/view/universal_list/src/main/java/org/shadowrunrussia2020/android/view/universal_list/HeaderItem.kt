package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_item.view.*

class HeaderItem(override val groupID: Int, val text: String, @DrawableRes val iconRes: Int?, val list: Collection<UniversalViewData>) : UniversalViewData() {
    override val isHeader = true
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = HeaderViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as HeaderViewHolder).bindView(this)
}

private class HeaderViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false))

    fun bindView(item: HeaderItem) {
        item.iconRes?.let { containerView.imageView.setImageDrawable(containerView.resources.getDrawable(it)) }
        containerView.titleView.text = item.text
        containerView.setOnClickListener {
            item.hide = !item.hide
            item.list.forEach { it.hide = item.hide }
            item.onNotifyDataSetChanged?.invoke()
        }
        containerView.arrow.setImageDrawable(
            containerView.resources.getDrawable(
                if (item.hide)
                    android.R.drawable.arrow_up_float
                else
                    android.R.drawable.arrow_down_float
            )
        )
    }
}