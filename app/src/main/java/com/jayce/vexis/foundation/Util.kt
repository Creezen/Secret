package com.jayce.vexis.foundation

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.creezen.commontool.Config.NIL
import com.creezen.commontool.bean.ActiveBean
import com.creezen.commontool.bean.FileBean
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.bean.UserBean
import com.creezen.commontool.toTime
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.ThreadTool.runOnMulti
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.domain.bean.ActiveEntry
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.domain.bean.FileEntry
import com.jayce.vexis.domain.bean.UserEntry
import kotlinx.coroutines.Dispatchers
import retrofit2.Call

object Util {

    const val TAG = "Util"

    inline fun <reified K : BaseService, T> request(
        crossinline func: suspend K.() -> Call<T>,
        crossinline callback: suspend (T) -> Unit
    ) {
        runOnMulti(Dispatchers.IO) {
            kotlin.runCatching {
                val result = func.invoke(NetTool.create()).await()
                ui { callback(result) }
            }.onFailure {
                Log.e(TAG, "Request network error: ${it.message}")
            }
        }
    }

    object Extension {
        fun FileBean.parcelable(): FileEntry {
            val hash = fileHash ?: NIL
            return FileEntry(fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, hash)
        }

        fun FileEntry.unParcelable() =
            FileBean(fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, fileHash)

        fun UserBean.parcelable(): UserEntry {
            return UserEntry(
                userId, nickname, name, age, sex, password, createTime, count, level, adminLevel,
                isEdit, email, selfIntroduction, phone, address, birthday, headType
            )
        }

        fun UserEntry.unParcelable(): UserBean {
            return UserBean(
                userId, nickname, name, age, sex, password, createTime, count, level, adminLevel,
                isEdit, email, selfIntroduction, phone, address, birthday, headType
            )
        }

        fun ActiveBean.parcelable(): ActiveEntry {
            return ActiveEntry(
                userID, nickname, createTime, level, adminLevel, support, against,
                inform, reported, follow, fans, post
            )
        }

        fun ActiveEntry.unParcelable(): ActiveBean {
            return ActiveBean(
                userID, nickname, createTime, level, adminLevel, support, against,
                inform, reported, follow, fans, post
            )
        }

        fun TelecomBean.room(): EventEntry {
            return EventEntry(type, userId, nickName, session, time,false, msgId, content)
        }

        fun EventEntry.telecom(): TelecomBean {
            return TelecomBean(type, userId, nickName, session, time, msgId, content)
        }

        fun EventEntry.chat(): ChatEntry {
            return ChatEntry(nickName, time.toTime(), content)
        }

        fun Boolean.onTrue(func: () -> Unit): Boolean {
            if (this) func.invoke()
            return this
        }

        fun Boolean.onFalse(func: () -> Unit): Boolean {
            if (!this) func.invoke()
            return this
        }

        fun ImageView.load(
            url: String,
            placeHolderId: Int? = null,
            key: String? = null,
            isCircle: Boolean = false
        ) {
            NetTool.setImage(
                context,
                this,
                url= url,
                placeHolderId = placeHolderId,
                key = key,
                isCircle = isCircle
            )
        }

        fun ImageView.load(
            url: String,
            placeHolder: Drawable? = null,
            key: String? = null,
            isCircle: Boolean = false
        ) {
            NetTool.setImage(
                context,
                this,
                url= url,
                placeHolder = placeHolder,
                key = key,
                isCircle = isCircle
            )
        }
    }
}