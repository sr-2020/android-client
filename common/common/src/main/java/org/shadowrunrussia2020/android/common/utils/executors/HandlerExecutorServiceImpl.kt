/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package org.shadowrunrussia2020.android.common.utils.executors

import android.os.Handler
import java.util.concurrent.*

/** A [ScheduledExecutorService] implementation.  */
class HandlerExecutorServiceImpl(private val mHandler: Handler) : AbstractExecutorService(), ScheduledExecutorService {
//    val isHandlerThread: Boolean get() = Thread.currentThread() === mHandler.looper.thread

    override fun shutdown(): Unit = throw UnsupportedOperationException()
    override fun shutdownNow(): List<Runnable> = throw UnsupportedOperationException()
    override fun isShutdown(): Boolean = false
    override fun isTerminated(): Boolean = false
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = throw UnsupportedOperationException()
    override fun execute(command: Runnable) { mHandler.post(command) }
    override fun <T> newTaskFor(runnable: Runnable, value: T): ScheduledFutureImpl<T> = ScheduledFutureImpl(runnable, value)
    override fun <T> newTaskFor(callable: Callable<T>): ScheduledFutureImpl<T> = ScheduledFutureImpl(callable)
    override fun submit(task: Runnable): ScheduledFuture<*> = submit(task, null as Void?)

    override fun <T> submit(task: Runnable, result: T): ScheduledFuture<T> {
        val future = newTaskFor(task, result)
        execute(future)
        return future
    }

    override fun <T> submit(task: Callable<T>): ScheduledFuture<T> {
        val future = newTaskFor(task)
        execute(future)
        return future
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        val future: ScheduledFutureImpl<*> = newTaskFor(command, null)
        mHandler.postDelayed(future, unit.toMillis(delay))
        return future
    }

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        val future = newTaskFor(callable)
        mHandler.postDelayed(future, unit.toMillis(delay))
        return future
    }

    override fun scheduleAtFixedRate(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledFuture<*>
            = throw UnsupportedOperationException()

    override fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): ScheduledFuture<*>
            = throw UnsupportedOperationException()

}