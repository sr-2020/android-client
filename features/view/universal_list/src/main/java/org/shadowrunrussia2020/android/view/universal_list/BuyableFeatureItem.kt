package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.buyable_feature_list_item.view.*
import org.shadowrunrussia2020.android.common.models.Feature

class BuyableFeatureItem(
    private val feature: Feature,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.buyable_feature_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        BuyableFeatureViewHolder(parent)

    override fun bindHolder(holder: RecyclerView.ViewHolder) =
        (holder as BuyableFeatureViewHolder).bindView(feature, onClick, hide)
}

private class BuyableFeatureViewHolder private constructor(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(
        LayoutInflater.from(parent.context).inflate(R.layout.buyable_feature_list_item, parent, false)
    )

    fun bindView(feature: Feature,  onClick: (() -> Unit)? = null, hide: Boolean) {
        containerView.mainText.text = feature.humanReadableName
        containerView.subText.text = feature.description
        containerView.price.text = feature.karmaCost.toString()
        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }
}