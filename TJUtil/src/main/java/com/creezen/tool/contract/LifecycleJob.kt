package com.creezen.tool.contract

import kotlinx.coroutines.CoroutineScope

interface LifecycleJob {

    suspend fun onDispatch()

    fun onTimeoutFinish(isWorkFinished: Boolean) {}
}