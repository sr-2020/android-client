package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.PassiveAbility
import java.util.*

fun createPassiveAbilityHeader(list:Collection<UniversalViewData>) = HeaderItem(R.id.passive_ability_item, "Пассивные способности", R.drawable.power, list)

class PassiveAbilityListItem(
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
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(passiveAbility: PassiveAbility, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.mainText.text = passiveAbility.name
        containerView.subText.text = passiveAbility.description
        val validUntil = passiveAbility.validUntil
        if (validUntil != null) {
            containerView.time.text =
                "Закончится " + PrettyTime(Locale("ru")).format(Date(validUntil))
        }

        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }
}