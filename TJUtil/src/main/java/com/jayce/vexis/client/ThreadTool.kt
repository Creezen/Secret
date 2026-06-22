package com.jayce.vexis.client

import com.jayce.vexis.client.ability.thread.BlockOption
import com.jayce.vexis.client.ability.thread.ThreadStatus
import com.jayce.vexis.client.ability.thread.ThreadType
import com.jayce.vexis.client.ability.thread.ThreadWrapper
import com.jayce.vexis.client.ability.thread.ThreadWrapperImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadTool {

    private lateinit var params: BaseTool.InitParam

    private val single by lazy {
        Executors.newSingleThreadExecutor { Thread(it, "TJ-S") }.asCoroutineDispatcher()
    }

    internal val multi by lazy {
        ThreadPoolExecutor(
            4,
            16,
            30,
            TimeUnit.SECONDS,
            SynchronousQueue(),
            ThreadFactory { Thread(it, "TJ-M") }
        ).asCoroutineDispatcher()
    }

    private val singleScope = CoroutineScope(single)

    private val multiScope = CoroutineScope(multi)

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val scopeMap = HashMap<String, CoroutineScope>()

    private val stackClassBlackList = listOf(
        "com.jayce.vexis.client.AndroidTool",
        "com.jayce.vexis.client.BaseTool",
        "com.jayce.vexis.client.DataTool",
        "com.jayce.vexis.client.FileTool",
        "com.jayce.vexis.client.NetTool",
        "com.jayce.vexis.client.SoundTool",
        "com.jayce.vexis.client.ThreadTool",
        "com.jayce.vexis.client.WindowTool",
        "com.jayce.vexis.foundation.Util"
        )

    fun init(initParam: BaseTool.InitParam) {
        params = initParam
    }

    suspend fun ui(block: suspend () -> Unit) {
        getCallInfo("ui")
        withContext(Dispatchers.Main) {
            block.invoke()
        }
    }

    suspend fun io(block: suspend () -> Unit) {
        getCallInfo("io")
        withContext(Dispatchers.IO) {
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

    fun runOnSingle(func: suspend (ThreadWrapperImpl) -> Unit): ThreadWrapper {
        getCallInfo("runOnSingle")
        val wrapper = ThreadWrapperImpl()
        singleScope.launch {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runOnMulti(func: suspend (ThreadWrapperImpl) -> Unit): ThreadWrapper {
        getCallInfo("runOnMulti")
        val wrapper = ThreadWrapperImpl()
        multiScope.launch {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runOnMain(func: suspend (ThreadWrapperImpl) -> Unit): ThreadWrapper {
        getCallInfo("runOnMain")
        val wrapper = ThreadWrapperImpl()
        mainScope.launch {
            runWithCatch(wrapper) {
                func.invoke(wrapper)
            }
        }
        return wrapper
    }

    fun runOnIO(func: suspend (ThreadWrapperImpl) -> Unit): ThreadWrapper {
        getCallInfo("runOnIO")
        val wrapper = ThreadWrapperImpl()
        ioScope.launch {
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
        getCallInfo("runOnSpecific")
        val wrapper = ThreadWrapperImpl()
        val scope = getScope(name)
        if(scope == null) {
            TLog.w("Scope $name not exists! You should register first")
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
        getCallInfo("runOnCurrent")
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
            TLog.w("delay time is ${option.delayMillis}, no need to block!")
            return defaultWrapper
        }
        return when (option.type) {
            ThreadType.SINGLE -> runOnSingle {
                blockCallback(it, option.delayMillis, onDispatch)
            }
            ThreadType.MULTI -> runOnMulti {
                blockCallback(it, option.delayMillis, onDispatch)
            }
            ThreadType.NAMED -> {
                if (option.name.isNullOrBlank()) {
                    TLog.w("name should not be null with type ${option.type}")
                    return defaultWrapper
                }
                runOnSpecific(option.name, option.dispatcher) {
                    blockCallback(it, option.delayMillis, onDispatch)
                }
            }
            ThreadType.CURRENT -> {
                if (option.scope == null) {
                    TLog.w("scope should not be null with type ${option.type}")
                    return defaultWrapper
                }
                runOnCurrent(option.scope, option.dispatcher) {
                    blockCallback(it, option.delayMillis, onDispatch)
                }
            }
            ThreadType.UNKNOW -> {
                TLog.d("Unknow type, do nothing!")
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
            TLog.w("name: $name exists!")
            return
        }
        scopeMap[name] = scope
    }

    fun getScope(name: String): CoroutineScope? {
        return scopeMap[name]
    }

    fun unregisterScope(name: String) {
        scopeMap[name]?.cancel()
        scopeMap.remove(name)
    }

    private fun getCallInfo(from: String) {
        if (!params.debugThread) return
        val stackTrace = Thread.currentThread().stackTrace
        val indexOfTargetStack = stackTrace.indexOfFirst {
            it.className.contains("com.jayce.vexis")
        }
        val element = stackTrace[indexOfTargetStack]
        TLog.w("($from - ${element.fileName}:${element.lineNumber}) [${element.className}] ${element.methodName}")
    }
}