package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.ethic_trigger_item.view.*
import kotlinx.android.synthetic.main.magic_spell_item.view.*
import org.shadowrunrussia2020.android.common.models.EthicTrigger
import org.shadowrunrussia2020.android.common.models.Spell

class EthicTriggerItem(
    private val trigger: EthicTrigger,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.ethic_3_trigger_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = EthicTriggerViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as EthicTriggerViewHolder).bindView(trigger, onClick, hide)
}

private class EthicTriggerViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.ethic_trigger_item, parent, false))

    fun bindView(trigger: EthicTrigger, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.mainText.text = trigger.description
        containerView.subText.text = if (trigger.kind == "principle") "Нажми, если нарушил" else "Нажми, если выполнил"
        containerView.setOnClickListener { onClick?.invoke() }
    }
}