package org.shadowrunrussia2020.android.common.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.common.utils.executors.HandlerExecutorServiceImpl
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author Dmitry Subbotenko
 */

object MainThreadSchedulers {

    /**
     *      Шедулер AndroidSchedulers.mainThread() обладает одним очень серьезным недостатком.
     *      Он не проверяет поток на котором исполняется.
     *
     *      Это приводит к неприятной блокировке потока например при выполнении кода типа
     *
     *      Observable.just(0)
     *               .observeOnMainThread(AndroidSchedulers.mainThread() )
     *               .subscribe{ do something }
     *
     *      Код do something выполнится в непредсказуемый момент времени, и если внутри него будет еще одно переключение на
     *      mainThread может вообще возникнуть дедлок главного потока.
     *      подробнее см. https://github.com/ReactiveX/RxAndroid/issues/335
     *
     *      Этот планировщик использует HandlerExecutorService от Фрески, с частичной перегрузкой методов,
     *      что до некоторой степени решает проблему.
     *
     */

    val androidUiScheduler get() = Schedulers.from(UIExecutor())
}

class UIExecutor(
    private val handler: Handler = Handler(Looper.getMainLooper())
) : ExecutorService by HandlerExecutorServiceImpl(handler) {

    private val commands: BlockingQueue<Runnable> = LinkedBlockingQueue()

    override fun execute(command: Runnable) =
        if (isUiThread()) {
            command.run()
        } else {
            postOnUi(command)
        }

    private fun postOnUi(command: Runnable) {
        commands.add(command)
        handler.postDelayed({ commands.forEach(Runnable::run); commands.clear() }, 1)
    }

    private fun isUiThread() = Thread.currentThread() === Looper.getMainLooper().thread
}

fun launchAsync(activity: Activity, f: (suspend () -> Unit)) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                f();
            }
        } catch (e: Exception) {
            showErrorMessage(activity, "${e.message}")
        }
    }
}