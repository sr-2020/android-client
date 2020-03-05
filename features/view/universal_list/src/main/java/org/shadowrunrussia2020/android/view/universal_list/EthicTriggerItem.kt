package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.magic_spell_item.view.*
import org.shadowrunrussia2020.android.common.models.EthicTrigger
import org.shadowrunrussia2020.android.common.models.Spell

class EthicTriggerItem(
    private val trigger: EthicTrigger,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.ethic_trigger_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = EthicTriggerViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as EthicTriggerViewHolder).bindView(trigger, onClick, hide)
}

private class EthicTriggerViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    // TODO(aeremin) Use dedicated layout
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.magic_spell_item, parent, false))

    fun bindView(trigger: EthicTrigger, onClick: (() -> Unit)?, hide: Boolean) {
        // TODO(aeremin) Use proper icon (or remove icon altogether)
        containerView.imageView.setImageDrawable(
            containerView.resources.getDrawable(R.drawable.enchanted)
        )

        containerView.titleView.text = "Поступок"
        containerView.contextView.text = trigger.description
        containerView.setOnClickListener { onClick?.invoke() }
    }
}