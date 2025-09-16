package com.jayce.vexis.foundation

import android.util.Log
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.commontool.bean.FileBean
import com.creezen.commontool.bean.UserBean
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.foundation.bean.FileEntry
import com.jayce.vexis.foundation.bean.UserEntry
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

    object Extension {
        fun FileBean.parcelable(): FileEntry {
            val hash = fileHash ?: EMPTY_STRING
            return FileEntry(fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, hash)
        }

        fun FileEntry.unParcelable() =
            FileBean(fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, fileHash)

        fun UserBean.parcelable(): UserEntry {
            return UserEntry(
                userId, nickname, name, age, sex, password, createTime, count, administrator,
                isEdit, email, selfIntroduction, phone, address, birthday, headType
            )
        }

        fun UserEntry.unParcelable(): UserBean {
            return UserBean(
                userId, nickname, name, age, sex, password, createTime, count, administrator,
                isEdit, email, selfIntroduction, phone, address, birthday, headType
            )
        }
    }
}