package com.jayce.vexis.business.article.paragraph

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.databinding.AddCommentLayoutBinding
import com.jayce.vexis.databinding.ParagraphItemLayoutBinding
import com.jayce.vexis.foundation.CustomDialog
import com.jayce.vexis.business.article.ArticleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParaAdapter(
    val context: Context,
    val activity: Activity,
    private val itemList: List<ParaRemarkBean>,
) : RecyclerView.Adapter<ParaAdapter.ViewHolder>() {
    private val list = arrayListOf(
            "表述不清", "内容啰嗦", "语法错误", "逻辑混乱", "前后矛盾", "缺少佐证"
        )

    private var articleId: Long = -1

    private val dialog by lazy {
        CustomDialog(
            context,
            AddCommentLayoutBinding.inflate(activity.layoutInflater),
        ).apply {
            setTitle("留言")
            setCancel(true)
            viewBinding.singleSelect.setChildLayout(list) {
                viewBinding.commentContent.hint = it
            }
        }
    }

    class ViewHolder(val binding: ParagraphItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val paragraph = binding.content
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ParagraphItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = itemList[position]
        holder.paragraph.setOnLongClickListener {
            it.setBackgroundColor(context.resources.getColor(R.color.BeanGreen, null))
            showCommentDialog(position, it)
            true
        }
        if (item.list.isEmpty()) {
            holder.paragraph.text = item.content.trim()
            return
        }
        displayComment(position, holder.paragraph)
    }

    override fun getItemCount() = itemList.size

    private fun displayComment(
        position: Int,
        textView: TextView,
    ) {
        val content = itemList[position].content.trim()
        val contentLength = content.length
        val imageSpan = ImageSpan(context, R.drawable.comment)
        val clickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val contentList = itemList[position].list
                    contentList[0].cotent.toast()
                }
            }
        val spanString = SpannableString("$content    ")
        spanString.setSpan(imageSpan, contentLength, contentLength + 4, ImageSpan.ALIGN_CENTER)
        spanString.setSpan(clickSpan, contentLength, contentLength + 4, ImageSpan.ALIGN_CENTER)
        textView.text = spanString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showCommentDialog(
        position: Int,
        view: View,
    ) {
        dialog.apply {
            LeftButton("取消") { _, _ ->
                view.setBackgroundColor(context.resources.getColor(R.color.white, null))
                dismiss()
            }
            RightButton("提交") { binding, _ ->
                view.setBackgroundColor(context.resources.getColor(R.color.white, null))
                ThreadTool.runOnMulti(Dispatchers.IO) {
                    val userId = user().userId
                    val paragraphId = itemList[position].paragraphId
                    val content = binding.commentContent.msg()
                    val result = NetTool.create<ArticleService>()
                        .postCommen(articleId, paragraphId, userId, content)
                        .await()
                    withContext(Dispatchers.Main) {
                        result.toast()
                    }
                }
                itemList[position].paragraphId.toast()
                dismiss()
            }
            show()
        }
    }

    fun setArticleId(articleId: Long) {
        this.articleId = articleId
    }
}