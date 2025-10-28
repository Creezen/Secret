package com.creezen.tool

import android.util.Log
import com.creezen.tool.ability.thread.BlockOption
import com.creezen.tool.ability.thread.LifecycleJob
import com.creezen.tool.ability.thread.ThreadStatus
import com.creezen.tool.ability.thread.ThreadType
import com.creezen.tool.ability.thread.ThreadWrapper
import com.creezen.tool.ability.thread.ThreadWrapperImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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

    private suspend fun runWithCatch(wrapper: ThreadStatus, func: suspend () -> Unit) {
        kotlin.runCatching {
            func.invoke()
        }.onFailure {
            wrapper.fail(it)
        }
    }

    fun runOnSingle(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend (ThreadWrapperImpl) -> Unit
    ): ThreadWrapper {
        val wrapper = ThreadWrapperImpl()
        singleScope.launch(dispatcher) {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runOnMulti(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend (ThreadWrapperImpl) -> Unit
    ): ThreadWrapper {
        val wrapper = ThreadWrapperImpl()
        multiScope.launch(dispatcher) {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runOnSpecific(
        name: String,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend (ThreadWrapperImpl) -> Unit
    ): ThreadWrapper {
        val wrapper = ThreadWrapperImpl()
        val scope = getScope(name)
        if(scope == null) {
            Log.w(TAG,"Scope $name not exists! You should register first")
            return wrapper
        }

        scope.launch(dispatcher) {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runOnCurrent(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend (ThreadWrapperImpl) -> Unit
    ): ThreadWrapper {
        val wrapper = ThreadWrapperImpl()
        scope.launch(dispatcher) {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runWithBlocking(option: BlockOption, onDispatch: () -> Unit): ThreadWrapper {
        val defaultWrapper = ThreadWrapperImpl()
        if (option.delayMillis <= 0) {
            Log.w(TAG, "delay time is ${option.delayMillis}, no need to block!")
            return defaultWrapper
        }
        return when (option.type) {
            ThreadType.SINGLE -> runOnSingle(option.dispatcher) {
                blockCallback(it, option.delayMillis, onDispatch)
            }
            ThreadType.MULTI -> runOnMulti {
                blockCallback(it, option.delayMillis, onDispatch)
            }
            ThreadType.NAMED -> {
                if (option.name.isNullOrBlank()) {
                    Log.w(TAG,"name should not be null with type ${option.type}")
                    return defaultWrapper
                }
                runOnSpecific(option.name, option.dispatcher) {
                    blockCallback(it, option.delayMillis, onDispatch)
                }
            }
            ThreadType.CURRENT -> {
                if (option.scope == null) {
                    Log.w(TAG,"scope should not be null with type ${option.type}")
                    return defaultWrapper
                }
                runOnCurrent(option.scope, option.dispatcher) {
                    blockCallback(it, option.delayMillis, onDispatch)
                }
            }
            ThreadType.UNKNOW -> {
                Log.d(TAG,"Unknow type, do nothing!")
                defaultWrapper
            }
        }
    }

    private suspend fun blockCallback(wrapper: ThreadStatus, delayMillis: Long, onDispatch: () -> Unit) {
        val result = withTimeoutOrNull(delayMillis) {
            onDispatch.invoke()
            wrapper.finished()
        }
        if (result == null) {
            wrapper.timeOut()
        }
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