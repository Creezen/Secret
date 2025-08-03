package com.creezen.tool

import android.util.Log
import com.creezen.tool.bean.BlockOption
import com.creezen.tool.contract.LifecycleJob
import com.creezen.tool.enum.ThreadType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors

object ThreadTool {

    private const val TAG = "ThreadTool"

    private val single by lazy {
        Executors.newSingleThreadExecutor { Thread(it, "TJ-S") }.asCoroutineDispatcher()
    }

    private val multi by lazy {
        Executors.newFixedThreadPool(16) { Thread(it, "TJ-M") }.asCoroutineDispatcher()
    }

    private val singleScope = CoroutineScope(single)

    private val multiScope = CoroutineScope(multi)

    private val scopeMap = HashMap<String, CoroutineScope>()

    fun init() {}

    suspend fun ui(block: () -> Unit) {
        withContext(Dispatchers.Main) {
            block.invoke()
        }
    }

    fun runOnSingle(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend () -> Unit
    ) {
        singleScope.launch(dispatcher) {
            func.invoke()
        }
    }

    fun runOnMulti(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend () -> Unit
    ) {
        multiScope.launch(dispatcher) {
            func.invoke()
        }
    }

    fun runOnSpecific(
        name: String,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend () -> Unit
    ) {
        val scope = getScope(name)
        if(scope == null) {
            Log.w(TAG,"Scope $name not exists! You should register first")
            return
        }
        scope.launch(dispatcher) {
            func.invoke()
        }
    }

    fun runOnCurrent(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend () -> Unit
    ) {
        scope.launch(dispatcher) {
            func.invoke()
        }
    }

    fun runWithBlocking(
        option: BlockOption,
        lifecycleJob: LifecycleJob
    ) {
        if (option.delayMillis <= 0) {
            Log.w(TAG, "delay time is ${option.delayMillis}, no need to block!")
            return
        }
        when (option.type) {
            ThreadType.SINGLE -> runOnSingle(option.dispatcher) {
                blockCallback(lifecycleJob, option.delayMillis)
            }
            ThreadType.MULTI -> runOnMulti {
                blockCallback(lifecycleJob, option.delayMillis)
            }
            ThreadType.NAMED -> {
                if (option.name.isNullOrBlank()) {
                    Log.w(TAG,"name should not be null with type ${option.type}")
                    return
                }
                runOnSpecific(option.name, option.dispatcher) {
                    blockCallback(lifecycleJob, option.delayMillis)
                }
            }
            ThreadType.CURRENT -> {
                if (option.scope == null) {
                    Log.w(TAG,"scope should not be null with type ${option.type}")
                    return
                }
                runOnCurrent(option.scope, option.dispatcher) {
                    blockCallback(lifecycleJob, option.delayMillis)
                }
            }
            ThreadType.UNKNOW -> Log.d(TAG,"Unknow type, do nothing!")
        }
    }

    private suspend fun blockCallback(lifecycleJob: LifecycleJob, delayMillis: Long) {
        val result = withTimeoutOrNull(delayMillis) {
            lifecycleJob.onDispatch()
        }
        lifecycleJob.onTimeoutFinish(result != null)
    }

    fun registerScope(name: String, scope: CoroutineScope) {
        if(scopeMap.containsKey(name)) {
            Log.w(TAG, "name: $name exists!")
            return
        }
        scopeMap[name] = scope
    }

    private fun getScope(name: String): CoroutineScope? {
        return scopeMap[name]
    }

    fun unregisterScope(name: String) {
        scopeMap[name]?.cancel()
        scopeMap.remove(name)
    }
}