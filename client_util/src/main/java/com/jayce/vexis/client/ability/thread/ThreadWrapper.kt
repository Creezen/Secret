package com.jayce.vexis.client.ability.thread

import kotlinx.coroutines.Job

interface ThreadWrapper {

    val launchJob: Job?

    fun onFailure(callback: (Throwable) -> Unit): ThreadWrapper

    fun onTimedOut(callback: () -> Unit): ThreadWrapper

    fun onComplete(callback: () -> Unit): ThreadWrapper

    fun cancel()
}