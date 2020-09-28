package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.Rent
import java.util.*

class RentListItem(
    private val rent: Rent,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.rent_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = RentViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as RentViewHolder).bindView(rent, onClick, hide)
}

private class RentViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(rent: Rent, onClick: (() -> Unit)?, hide: Boolean) {
        containerView.mainText.text = rent.finalPrice.toString()
        containerView.subText.text = rent.nomenklaturaName
        containerView.time.text = PrettyTime(Locale("ru")).format(rent.dateCreated)

        containerView.setOnClickListener {
            onClick?.invoke()
        }
    }
}
