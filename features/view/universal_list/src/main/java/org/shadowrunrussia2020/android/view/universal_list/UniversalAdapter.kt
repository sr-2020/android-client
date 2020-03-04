package org.shadowrunrussia2020.android.view.universal_list

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

abstract class UniversalViewData {
    abstract val groupID: Int
    abstract val isHeader: Boolean
    val viewType get() = if (isHeader) -groupID else groupID
    abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun bindHolder(holder: RecyclerView.ViewHolder)
    var hide = false
    var onNotifyDataSetChanged: (() -> Unit)? = null
}

class UniversalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<UniversalViewData>()
    private val comparator = object : Comparator<UniversalViewData> {
        override fun compare(o1: UniversalViewData?, o2: UniversalViewData?): Int {
            if (o1 === o2) return 0
            o1 ?: return -1
            o2 ?: return 1

            return if (o1.groupID == o2.groupID) {
                when {
                    o1.isHeader == o2.isHeader -> 0
                    o1.isHeader -> -1
                    else -> 1
                }
            } else
                o1.groupID - o2.groupID
        }
    }

    fun clear() = list.clear()

    fun appendList(items: Collection<UniversalViewData>) {
        items.forEach { it.onNotifyDataSetChanged = { apply() } }
        list.addAll(items)

    }


    fun apply() {
        list.sortWith(comparator)
        activeList = list.filter { it.isHeader || !it.hide }

        notifyDataSetChanged()

    }

    var activeList: List<UniversalViewData> = listOf()

    override fun getItemViewType(position: Int): Int = activeList[position].viewType
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = activeList.first { it.viewType == viewType }.createViewHolder(parent)
    override fun getItemCount(): Int = activeList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = activeList[position].bindHolder(holder)
}