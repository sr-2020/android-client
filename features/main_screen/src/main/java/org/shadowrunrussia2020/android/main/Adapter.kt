package org.shadowrunrussia2020.android.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_character_view_item.view.*
import org.shadowrunrussia2020.android.common.models.PassiveAbility

class AbilityAdapter: ListAdapter<PassiveAbility, AbilityViewHolder>(PassiveAbility.Companion) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbilityViewHolder  = AbilityViewHolder(parent)
    override fun onBindViewHolder(holder: AbilityViewHolder, position: Int) = holder.bindItem(getItem(position))
}

class AbilityViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.main_character_view_item, parent, false))

    fun bindItem(data: PassiveAbility){
        containerView.textHeader.text = data.name
        containerView.textSource.text = data.description
    }
}
