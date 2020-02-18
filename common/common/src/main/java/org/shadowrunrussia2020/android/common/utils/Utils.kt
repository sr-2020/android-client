package org.shadowrunrussia2020.android.common.utils

import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

/**
 * @author Subbotenko Dmitry
 */
operator fun DisposableContainer.plusAssign(d: Disposable) {
    add(d)
}