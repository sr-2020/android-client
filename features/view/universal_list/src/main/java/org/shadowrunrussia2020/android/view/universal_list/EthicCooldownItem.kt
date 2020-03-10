package org.shadowrunrussia2020.android.view.universal_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.ethic_trigger_item.view.*
import org.shadowrunrussia2020.android.common.utils.MainThreadSchedulers
import org.shadowrunrussia2020.android.common.utils.plusAssign
import java.util.concurrent.TimeUnit

class EthicCooldownItem(
    private val lockedUntil: Long,
    private val onCooldownEnd: (() -> Unit)
) : UniversalViewData() {
    val disposer = CompositeDisposable()
    override val groupID = R.id.ethic_2_cooldown_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        EthicCooldownHolder(parent)

    override fun bindHolder(holder: RecyclerView.ViewHolder) =
        (holder as EthicCooldownHolder).bindView(disposer, lockedUntil, onCooldownEnd)
}

private class EthicCooldownHolder private constructor(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {


    constructor(parent: ViewGroup)
            : this(
        LayoutInflater.from(parent.context).inflate(R.layout.ethic_cooldown_item, parent, false)
    )

    fun bindView(disposer: CompositeDisposable, lockedUntil: Long, onCooldownEnd: (() -> Unit)) {
        disposer += Observable.interval(0, 1, TimeUnit.SECONDS)
            .observeOn(MainThreadSchedulers.androidUiScheduler)
            .subscribe {
                if (lockedUntil <= System.currentTimeMillis()) {
                    onCooldownEnd.invoke()
                    disposer.clear()
                } else {
                    containerView.mainText.text = itemView.context.getString(
                        R.string.ethics_cooldown_message,
                        org.apache.commons.lang3.time.DurationFormatUtils.formatDuration(
                            lockedUntil - System.currentTimeMillis(),
                            "mm:ss"
                        ))
                }
            }
    }
}