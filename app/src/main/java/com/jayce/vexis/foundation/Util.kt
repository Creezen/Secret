package com.jayce.vexis.foundation

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.jayce.vexis.client.NetTool
import com.jayce.vexis.client.NetTool.await
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.client.bean.ImageOption
import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.domain.bean.ActiveEntry
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.domain.bean.FileEntry
import com.jayce.vexis.domain.bean.UserEntry
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.bean.ActiveBean
import com.jayce.vexis.util.bean.FileBean
import com.jayce.vexis.util.bean.TelecomBean
import com.jayce.vexis.util.bean.UserBean
import com.jayce.vexis.util.toTime
import retrofit2.Call

object Util {

    inline fun <reified K : BaseService, T> request(
        crossinline func: suspend K.() -> Call<T>,
        crossinline callback: suspend (T) -> Unit
    ) {
        runOnIO {
            kotlin.runCatching {
                val result = func.invoke(NetTool.create()).await()
                ui { callback(result) }
            }.onFailure {
                it.printStackTrace()
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
            return ChatEntry(nickName, time.toTime(), content, id, isRead)
        }

        fun Boolean.onTrue(func: () -> Unit): Boolean {
            if (this) func.invoke()
            return this
        }

        fun Boolean.onFalse(func: () -> Unit): Boolean {
            if (!this) func.invoke()
            return this
        }

        fun ImageView.load(url: String, option: ImageOption = ImageOption()) {
            NetTool.setImage(context, this, url, option)
        }

        fun Activity.jumpTo(onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent()
            onJump?.invoke(intent)
            startActivity(intent)
        }

        fun Activity.jumpTo(cls: Class<*>, onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent(this, cls)
            onJump?.invoke(intent)
            startActivity(intent)
        }

        fun Activity.jumpTo(action: String, onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent(action)
            onJump?.invoke(intent)
            startActivity(intent)
        }

        fun Fragment.jumpTo(onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent()
            onJump?.invoke(intent)
            startActivity(intent)
        }

        fun Fragment.jumpTo(cls: Class<*>, onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent(context, cls)
            onJump?.invoke(intent)
            startActivity(intent)
        }

        fun Fragment.jumpTo(action: String, onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent(action)
            onJump?.invoke(intent)
            startActivity(intent)
        }
    }
}