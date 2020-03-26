package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.implant_item.view.*
import org.shadowrunrussia2020.android.common.models.Implant

class ImplantItem(
    private val implant: Implant,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.implant_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ImplantViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as ImplantViewHolder).bindView(implant, onClick, hide)
}

private class ImplantViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.implant_item, parent, false))

    fun bindView(implant: Implant, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.implantName.text = implant.name
        containerView.implantSlotAndGrade.text = "${implant.slot} ${implant.grade}"
        containerView.setOnClickListener { onClick?.invoke() }
    }
}