package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.simple_header_text.view.*
import org.shadowrunrussia2020.android.common.models.ActiveAbility

fun createActiveAbilityHeader(list:Collection<UniversalViewData>) = HeaderItem(R.id.active_ability_item, "Активируемые способности", R.drawable.statement, list)

class ActiveAbilitySpellItem(
    private val activeAbility: ActiveAbility,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.active_ability_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ActiveAbilityViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as ActiveAbilityViewHolder).bindView(activeAbility, onClick, hide)
}

private class ActiveAbilityViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.simple_header_text, parent, false))

    fun bindView(activeAbility: ActiveAbility, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.textHeader.text = activeAbility.humanReadableName
        containerView.textSource.text = activeAbility.description
        containerView.setOnClickListener { onClick?.invoke() }
    }
}