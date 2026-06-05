package com.jayce.vexis.business.article

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import com.creezen.commontool.Config.MEDIA_TYPE_IMAGE
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivitySynergyEditBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.image.ScaleImage

class ArticleEditActivity : BaseActivity<ActivitySynergyEditBinding>() {

    private var lastClickChild: Int = -1
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        launcher = getLauncher(openFile()) {
            if (it == null) return@getLauncher
            val image = ScaleImage(this, false, null)
            image.setPreview(true)
            image.setImageURI(it)
            if (binding.container.childCount == 0) {
                binding.container.addView(image)
            } else {
                binding.container.addView(image, lastClickChild + 1)
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
            val paragraphs = getParagraphList(binding.content)
            request<ArticleService, Boolean>({
                postSynergy(title, paragraphs, liveUser.userId)
            }) { if (it) finish() }
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
                binding.container.removeViewAt(lastClickChild)
            }
            KeyEvent.KEYCODE_ENTER -> {
                if (child is EditText) return super.dispatchKeyEvent(event)
                val editText = EditText(this)
                binding.container.addView(editText, lastClickChild + 1)
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

    private fun getParagraphList(textView: TextView): ArrayList<String> {
        val msg = textView.msg(true)
        val paragraphSequence = msg.split("\n").asSequence().filterNot { it.isEmpty() }
        val list = arrayListOf<String>()
        list.addAll(paragraphSequence.toList())
        return list
    }
}