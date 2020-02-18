/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package org.shadowrunrussia2020.android.common.utils.executors

import android.os.Handler
import java.util.concurrent.*

/** A [ScheduledFuture] for [HandlerExecutorServiceImpl].  */
class ScheduledFutureImpl<V> constructor(private val mListenableFuture: FutureTask<V>) : RunnableFuture<V>, ScheduledFuture<V> {

    constructor( callable: Callable<V>): this(FutureTask(callable))
    constructor( runnable: Runnable, result: V): this(FutureTask(runnable, result))

    override fun getDelay(unit: TimeUnit): Long = throw UnsupportedOperationException()
    override fun compareTo(other: Delayed): Int = throw UnsupportedOperationException()
    override fun run() = mListenableFuture.run()
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean = mListenableFuture.cancel(mayInterruptIfRunning)
    override fun isCancelled(): Boolean = mListenableFuture.isCancelled
    override fun isDone(): Boolean = mListenableFuture.isDone

    @Throws(InterruptedException::class, ExecutionException::class)
    override fun get(): V? = mListenableFuture.get()

    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    override fun get(timeout: Long, unit: TimeUnit): V? = mListenableFuture[timeout, unit]
}