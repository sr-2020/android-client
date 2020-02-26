package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.magic_spell_item.view.*
import org.shadowrunrussia2020.android.common.models.Spell

fun createMagicBookHeader(list:Collection<UniversalViewData>) = HeaderItem(R.id.magic_item, "Заклинания", R.drawable.magic_book, list)

class MagicSpellItem(
    private val spell: Spell,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.magic_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = MagicViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as MagicViewHolder).bindView(spell, onClick, hide)
}

private class MagicViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.magic_spell_item, parent, false))

    fun bindView(spell: Spell, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.imageView.setImageDrawable(
            if (spell.hasTarget)
                containerView.resources.getDrawable(R.drawable.enchanted)
            else
                containerView.resources.getDrawable(R.drawable.magic_vand)
        )

        containerView.titleView.text = spell.humanReadableName
        containerView.contextView.text = spell.description
        containerView.setOnClickListener { onClick?.invoke() }

    }
}