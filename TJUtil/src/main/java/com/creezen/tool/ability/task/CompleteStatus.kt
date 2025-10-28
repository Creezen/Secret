package com.creezen.tool.ability.task

import java.util.concurrent.atomic.AtomicBoolean

class  CompleteStatus <T> {

    private val isWait = AtomicBoolean(false)
    private val isComplete = AtomicBoolean(false)
    private var task: T? = null

    @Synchronized
    fun wait(cacheAny: T? = null) {
        isWait.set(true)
        task = cacheAny
    }

    @Synchronized
    fun isWaiting(): Boolean {
        return isWait.get()
    }

    @Synchronized
    fun complete() {
        isComplete.set(true)
    }

    @Synchronized
    fun isComplete(): Boolean {
        return isComplete.get()
    }

    fun job(): T? = task

    @Synchronized
    fun tryOrWait(task: T? = null, callback: () -> Unit) {
        if (isComplete()) {
            callback.invoke()
        } else {
            wait(task)
        }
    }

    @Synchronized
    fun setOrComplete(callback: (T) -> Unit) {
        complete()
        if (isWaiting()) {
            job()?.let {
                callback.invoke(it)
            }
        }
    }
}