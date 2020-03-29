package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.ActiveAbility
import java.util.*

fun createActiveAbilityHeader(list:Collection<UniversalViewData>) = HeaderItem(R.id.active_ability_item, "Активируемые способности", R.drawable.statement, list)

class ActiveAbilityListItem(
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
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(activeAbility: ActiveAbility, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.mainText.text = activeAbility.humanReadableName
        containerView.subText.text = activeAbility.description
        val validUntil = activeAbility.validUntil
        if (validUntil != null) {
            containerView.time.text =
                "Закончится " + PrettyTime(Locale("ru")).format(Date(validUntil))
        }

        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }
}