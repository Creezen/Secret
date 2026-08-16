package com.jayce.vexis.foundation.ui.block.mention

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ability.thread.ThreadWrapper
import com.jayce.vexis.databinding.MentionChooseDialogBinding
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.TextListener
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.FlexibleDialog

class MentionEditText(context: Context, attr: AttributeSet?) : AppCompatEditText(context, attr) {

    init {
        movementMethod = LinkMovementMethod()
    }

    companion object {
        private const val SEARCH_DELAY = 300L
    }

    private var lastSearchTime: Long = 0L
    private var threadWrapper: ThreadWrapper? = null
    private val adapter = MentionSearchAdapter(context, listOf())

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)
        if (s == null) return
        val isInsertMention = before == 0 && count == 1 && s[start] == '@'
        if (!isInsertMention) return
        showMentionDialog(start)
    }

    private fun showMentionDialog(start: Int) {
        FlexibleDialog
            .flexibleView<MentionChooseDialogBinding>(context) {
                initDialogView(start, this, it)
            }
            .title("请选择")
            .show()
    }

    private fun initDialogView(
        start: Int,
        binding: MentionChooseDialogBinding,
        dialog: FlexibleDialog<MentionChooseDialogBinding>,
    ) {
        binding.searchList.adapter = adapter
        binding.searchList.layoutManager = LinearLayoutManager(context)
        adapter.setOnItemClick {
            val span = getMentionSpan(it.profile.nickname, it.userId)
            text?.replace(start, start + 1, span)
            dialog.dismiss()
        }
        binding.searchContent.addTextChangedListener(object : TextListener() {
            override fun afterTextChanged(s: Editable?) {
                if (s == null || s.toString().isEmpty()) {
                    binding.searchList.visibility = View.GONE
                    binding.emptyResult.visibility = View.VISIBLE
                    return
                }
                val current = System.currentTimeMillis()
                if (current - lastSearchTime < SEARCH_DELAY) return
                lastSearchTime = current
                threadWrapper?.cancel()
                threadWrapper = request<UserService, _>({ queryUserByContent(s.toString()) }) {
                    if (it.isEmpty()) {
                        binding.searchList.visibility = View.GONE
                        binding.emptyResult.visibility = View.VISIBLE
                    } else {
                        binding.searchList.visibility = View.VISIBLE
                        binding.emptyResult.visibility = View.GONE
                        adapter.notifyDataChange(it)
                    }
                }
            }
        })
    }

    private fun getMentionSpan(name: String, userId: String): SpannableString {
        val str = "@$name"
        val span = URLSpan("app://user/$userId")
        val spanString = SpannableString(str)
        spanString.setSpan(span, 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanString
    }

    fun content(): List<String> {
        val editable = text ?: return listOf()
        val contentList = arrayListOf<String>()
        val indexes = getLineSplitList(editable)
        indexes.forEachIndexed { index, i ->
            val startIndex = if (index == 0) 0 else indexes[index - 1]
            val spanContent = editable.subSequence(startIndex, i) as Spanned
            val text = Html.toHtml(spanContent, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
            contentList.add(text)
        }
        return contentList
    }

    private fun getLineSplitList(editable: Editable): List<Int> {
        var index = editable.mapIndexedNotNull { index, c -> if (c == '\n') index else null }
        if (index.isEmpty()) index = index + editable.length
        if(index.isNotEmpty() && index.last() < editable.length - 1) {
            index = index + editable.length
        }
        return index
    }
}