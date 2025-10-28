package com.creezen.tool.ability.thread

import com.creezen.tool.ability.task.CompleteStatus

class ThreadWrapperImpl : ThreadWrapper, ThreadStatus {

    private var failCallback: ((Throwable) -> Unit)? = null
    private var finishCallback: (() -> Unit)? = null
    private var timeoutCallback: (() -> Unit)? = null
    private val failStatus = CompleteStatus<Throwable>()
    private val finishStatus = CompleteStatus<Unit>()
    private val timeoutStatus = CompleteStatus<Unit>()

    override fun fail(throwable: Throwable) {
        failStatus.tryOrWait(throwable) {
            failCallback?.invoke(throwable)
        }
    }

    override fun onFailure(callback: (Throwable) -> Unit): ThreadWrapper {
        this.failCallback = callback
        failStatus.setOrComplete {
            failCallback?.invoke(it)
        }
        return this
    }

    override fun timeOut() {
        timeoutStatus.tryOrWait {
            timeoutCallback?.invoke()
        }
    }

    override fun onTimedOut(callback: () -> Unit): ThreadWrapper {
        this.timeoutCallback = callback
        timeoutStatus.setOrComplete {
            timeoutCallback?.invoke()
        }
        return this
    }

    override fun finished() {
        finishStatus.tryOrWait {
            finishCallback?.invoke()
        }
    }

    override fun onComplete(callback: () -> Unit): ThreadWrapper {
        this.finishCallback = callback
        finishStatus.setOrComplete {
            finishCallback?.invoke()
        }
        return this
    }
}