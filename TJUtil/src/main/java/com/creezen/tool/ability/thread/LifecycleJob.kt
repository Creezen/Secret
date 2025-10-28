package com.creezen.tool.ability.thread

interface LifecycleJob {

    suspend fun onDispatch()

    fun onTimeoutFinish(isWorkFinished: Boolean) {}
}