package com.jayce.vexis.foundation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.jayce.vexis.client.NetTool
import com.jayce.vexis.client.NetTool.await
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.client.ThreadTool.runWithBlocking
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.client.ability.thread.BlockOption
import com.jayce.vexis.client.ability.thread.ThreadWrapper
import com.jayce.vexis.client.ability.thread.ThreadWrapperImpl
import com.jayce.vexis.client.bean.ImageOption
import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.domain.bo.ActiveBO
import com.jayce.vexis.domain.bo.ChatBO
import com.jayce.vexis.domain.database.event.EventEntity
import com.jayce.vexis.domain.database.file.FileEntity
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.toTime
import com.jayce.vexis.util.vo.ActiveVO
import com.jayce.vexis.util.vo.EventVO
import com.jayce.vexis.util.vo.FileVO
import retrofit2.Call

object Util {

    inline fun <reified K : BaseService, T> request(
        crossinline func: suspend K.() -> Call<T>,
        option: BlockOption? = null,
        crossinline callback: suspend (T) -> Unit
    ): ThreadWrapper {
        val wrapper = ThreadWrapperImpl()
        val action: suspend () -> Unit = {
            val result = func.invoke(NetTool.create()).await()
            ui { callback(result) }
        }
        val innerWrapper = if (option != null) {
            runWithBlocking(option, action)
        } else {
            runOnIO { action.invoke() }
        }
        innerWrapper.onFailure { wrapper.fail(it) }
            .onTimedOut { wrapper.timeOut() }
        return wrapper
    }

    object Extension {
        fun EventEntity.chat(): ChatBO {
            return ChatBO(nickName, time.toTime(), content, msgId.toLong(), isRead)
        }

        fun FileEntity.vo(): FileVO {
            return FileVO(userId, fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, fileHash)
        }

        fun FileVO.entity(): FileEntity {
            return FileEntity(userId, fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, fileHash)
        }

        fun ActiveBO.vo(): ActiveVO {
            return ActiveVO(userID, nickname, createTime, level, adminLevel, support, against, inform, reported, follow, fans, post)
        }

        fun ActiveVO.bo(): ActiveBO {
            return ActiveBO(userID, nickname, createTime, level, adminLevel, support, against, inform, reported, follow, fans, post)
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

        fun Context.jumpTo(cls: Class<*>, onJump: (Intent.() -> Unit)? = null) {
            val intent = Intent(this, cls)
            onJump?.invoke(intent)
            startActivity(intent)
        }
    }
}