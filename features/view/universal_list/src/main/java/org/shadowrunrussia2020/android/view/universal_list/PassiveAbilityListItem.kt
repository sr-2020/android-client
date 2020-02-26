package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.simple_header_text.view.*
import org.shadowrunrussia2020.android.common.models.PassiveAbility

fun createPassiveAbilityHeader(list:Collection<UniversalViewData>) = HeaderItem(R.id.passive_ability_item, "Пассивные способности", R.drawable.power, list)

class PassiveAbilitySpellItem(
    private val passiveAbility: PassiveAbility,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.passive_ability_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = PassiveAbilityViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as PassiveAbilityViewHolder).bindView(passiveAbility, onClick, hide)
}

private class PassiveAbilityViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.simple_header_text, parent, false))

    fun bindView(passiveAbility: PassiveAbility, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.textHeader.text = passiveAbility.name
        containerView.textSource.text = passiveAbility.description
        containerView.setOnClickListener { onClick?.invoke() }

    }
}