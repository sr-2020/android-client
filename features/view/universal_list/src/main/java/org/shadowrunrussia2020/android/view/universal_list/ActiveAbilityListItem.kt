package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.shadowrunrussia2020.android.common.models.ActiveAbility
import org.shadowrunrussia2020.android.common.utils.MainThreadSchedulers
import org.shadowrunrussia2020.android.common.utils.plusAssign
import org.shadowrunrussia2020.android.common.utils.timeBeforeInMinutes
import java.util.concurrent.TimeUnit

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
    private val disposer = CompositeDisposable()

    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(activeAbility: ActiveAbility, onClick: (() -> Unit)?, hide: Boolean) {
        disposer.clear()

        containerView.mainText.text = activeAbility.humanReadableName
        containerView.subText.text = activeAbility.description

        containerView.setOnClickListener {
            onClick?.invoke()
        }

        disposer += Observable.interval(0, 10, TimeUnit.SECONDS)
            .observeOn(MainThreadSchedulers.androidUiScheduler)
            .subscribe {
                val currentTimestamp = System.currentTimeMillis()
                val validUntil = activeAbility.validUntil
                if (validUntil != null) {
                    containerView.time.text =
                        "Закончится через ${timeBeforeInMinutes(currentTimestamp, validUntil)}"
                } else if (activeAbility.cooldownUntil >= currentTimestamp) {
                    containerView.time.text =
                        "Доступно через ${timeBeforeInMinutes(currentTimestamp, activeAbility.cooldownUntil)}"
                } else {
                    containerView.time.text = ""
                }
            }
    }
}

