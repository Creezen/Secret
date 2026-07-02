package com.jayce.vexis.business.article.article

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.children
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.business.article.OnContentChangeListener
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.FileTool
import com.jayce.vexis.client.NetTool
import com.jayce.vexis.client.TLog
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivitySynergyEditBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.image.ScaleImage
import com.jayce.vexis.util.Config.MEDIA_TYPE_IMAGE
import com.jayce.vexis.util.bean.ArticleContentBean
import com.jayce.vexis.util.toJson
import okhttp3.MultipartBody

class ArticleEditActivity : BaseActivity<ActivitySynergyEditBinding>() {

    companion object {
        private const val TYPE_TEXT = 0
        private const val TYPE_IMAGE = 1
        private const val TYPE_CODE = 2
    }

    private var lastClickChild: Int = -1
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    private val listener = object : OnContentChangeListener{
        override fun onRemove(index: Int) {
            binding.container.removeViewAt(index)
        }

        override fun onAdd(type: Int, index: Int, view: View) {
            binding.container.addView(view, index)
        }
    }

    override fun registerLauncher() {
        launcher = getLauncher(openFile()) {
            if (it == null) return@getLauncher
            val image = ScaleImage(this, false, null)
            image.setPreview(true)
            image.setImageURI(it)
            if (binding.container.childCount == 0) {
                listener.onAdd(TYPE_IMAGE, 0, image)
            } else {
                listener.onAdd(TYPE_IMAGE, lastClickChild + 1, image)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    private fun initPage() = binding.apply {
        submit.setOnClickListener {
            val title = binding.title.msg(true)
            if (title.length < 3) {
                "标题最少三个字符".toast()
                return@setOnClickListener
            }
            "上传中".toast()
            val paragraphs = getContentList()
            val pair = buildRequestList(paragraphs)
            request<ArticleService, Boolean>(
                { postArticle(title, liveUser.userId,  pair.first.toJson(), pair.second) }
            ) {
                TLog.d("upload result: $it")
                if (it) finish()
            }
        }
        photo.setOnClickListener {
            launcher?.launch(arrayOf(MEDIA_TYPE_IMAGE))
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return super.dispatchKeyEvent(event)
        val child = currentFocus ?: return super.dispatchKeyEvent(event)
        val index = binding.container.indexOfChild(child)
        if (index < 0) return super.dispatchKeyEvent(event)
        when (event.keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                if (child is EditText && child.text.isNotEmpty()) {
                    return super.dispatchKeyEvent(event)
                }
                listener.onRemove(lastClickChild)
            }
            KeyEvent.KEYCODE_ENTER -> {
                if (child is EditText) return super.dispatchKeyEvent(event)
                val editText = EditText(this)
                listener.onAdd(TYPE_TEXT, lastClickChild + 1, editText)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val container = binding.container
        if (event == null) return super.dispatchTouchEvent(event)
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return super.dispatchTouchEvent(event)

        val globalRect = Rect()
        container.getGlobalVisibleRect(globalRect)
        if (!globalRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            return super.dispatchTouchEvent(event)
        }

        val containerLocation = IntArray(2)
        container.getLocationOnScreen(containerLocation)
        val relativeX = event.rawX - containerLocation[0]
        val relativeY = event.rawY - containerLocation[1]
        for (i in container.childCount - 1 downTo 0) {
            val child = container.getChildAt(i)
            if (child.visibility != View.VISIBLE) continue
            val rect = Rect()
            child.getHitRect(rect)
            if (rect.contains(relativeX.toInt(), relativeY.toInt())) {
                lastClickChild = i
                child.isFocusableInTouchMode = true
                child.requestFocus()
                val manager = getSystemService(InputMethodManager::class.java)
                manager.showSoftInput(child, InputMethodManager.SHOW_FORCED)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun getContentList(): List<ArticleContentBean> {
        val contentList = arrayListOf<ArticleContentBean>()
        binding.container.children.forEach { view ->
            when (view) {
                is EditText -> {
                    val msgList = view.msg().split("\n")
                    val sequence = msgList.asSequence().filterNot { it.isEmpty() }
                    sequence.forEach {
                        contentList.add(ArticleContentBean(TYPE_TEXT, it))
                    }
                }
                is ScaleImage -> {
                    val uri = view.imageUri ?: return@forEach
                    contentList.add(ArticleContentBean(TYPE_IMAGE, uri.toString()))
                }
            }
        }
        return contentList
    }

    private fun buildRequestList(
        list: List<ArticleContentBean>
    ): Pair<List<ArticleContentBean>, List<MultipartBody.Part>> {
        TLog.d("buildRequestList: ${list.size}")
        val textList = arrayListOf<ArticleContentBean>()
        val partList = arrayListOf<MultipartBody.Part>()
        list.forEach {
            when (it.type) {
                TYPE_TEXT -> textList.add(it)
                TYPE_IMAGE -> {
                    val uri = Uri.parse(it.content)
                    textList.add(ArticleContentBean(it.type, FileTool.getFileNameByUri(uri)))
                    partList.add(NetTool.buildUriMultipart(it.content, "articleFile"))
                }
            }
        }
        return textList to partList
    }
}