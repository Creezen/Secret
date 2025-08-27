package com.jayce.vexis.foundation

import android.util.Log
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.base.BaseService
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import java.util.concurrent.CompletableFuture

object Util {

    const val TAG = "Util"

    inline fun <reified K : BaseService, T> request(
        crossinline func: suspend K.() -> Call<T>,
        crossinline callback: suspend (T) -> Unit
    ) {
        val future = CompletableFuture<T>()
        ThreadTool.runOnMulti(Dispatchers.IO) {
            kotlin.runCatching {
                val result = func.invoke(NetTool.create())
                future.complete(result.await())
                callback(future.get())
            }.onFailure {
                Log.e(TAG, "Request network error: ${it.message}")
            }
        }
    }
}