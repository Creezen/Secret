package com.creezen.tool.ability.thread

interface ThreadWrapper {

    fun onFailure(callback: (Throwable) -> Unit): ThreadWrapper

    fun onTimedOut(callback: () -> Unit): ThreadWrapper

    fun onComplete(callback: () -> Unit): ThreadWrapper

}