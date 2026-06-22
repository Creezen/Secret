package com.jayce.vexis.client.ability.thread

interface ThreadWrapper {

    fun onFailure(callback: (Throwable) -> Unit): ThreadWrapper

    fun onTimedOut(callback: () -> Unit): ThreadWrapper

    fun onComplete(callback: () -> Unit): ThreadWrapper

}