package org.shadowrunrussia2020.android.view.universal_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.generic_recycler_view_item.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.common.models.Timer
import org.shadowrunrussia2020.android.common.utils.MainThreadSchedulers
import org.shadowrunrussia2020.android.common.utils.plusAssign
import java.util.*
import java.util.concurrent.TimeUnit

class TimerListItem(
    private val modelTimestamp: Long,
    private val timer: Timer,
    private val onClick: (() -> Unit)? = null
) : UniversalViewData() {
    override val groupID = R.id.timer_item
    override val isHeader = false
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = TimerViewHolder(parent)
    override fun bindHolder(holder: RecyclerView.ViewHolder) = (holder as TimerViewHolder).bindView(timer, modelTimestamp, onClick, hide)
}

private class TimerViewHolder private constructor(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    private val disposer = CompositeDisposable()

    constructor(parent: ViewGroup)
            : this(LayoutInflater.from(parent.context).inflate(R.layout.generic_recycler_view_item, parent, false))

    fun bindView(timer: Timer, modelTimestamp: Long, onClick: (() -> Unit)?, hide: Boolean) {
        disposer.clear()

        containerView.mainText.text = timer.description

        containerView.setOnClickListener {
            onClick?.invoke()
        }

        disposer += Observable.interval(0, 10, TimeUnit.SECONDS)
            .observeOn(MainThreadSchedulers.androidUiScheduler)
            .subscribe {
                val t = modelTimestamp + timer.miliseconds
                containerView.subText.text = PrettyTime(Locale("ru")).format(Date(t))
            }
    }
}
