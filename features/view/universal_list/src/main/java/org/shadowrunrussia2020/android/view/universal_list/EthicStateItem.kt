package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.ethic_trigger_item.view.*
import org.shadowrunrussia2020.android.common.models.EthicState

class EthicStateItem(
    private val state: EthicState
) : UniversalViewData() {
    override val groupID = R.id.ethic_1_state_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        EthicStateViewHolder(parent)

    override fun bindHolder(holder: RecyclerView.ViewHolder) =
        (holder as EthicStateViewHolder).bindView(state, hide)
}

private class EthicStateViewHolder private constructor(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(
        LayoutInflater.from(parent.context).inflate(R.layout.ethic_trigger_item, parent, false)
    )

    fun bindView(state: EthicState, hide: Boolean) {
        containerView.mainText.text = mapOf(
            "control" to "Контроль",
            "individualism" to "Индивидуализм",
            "mind" to "Разум",
            "violence" to "Насилие"
        ).get(state.scale) + ": " + state.value
        containerView.subText.text = state.description
    }
}