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
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.R
import com.jayce.vexis.core.Config
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.databinding.AddCommentLayoutBinding
import com.jayce.vexis.databinding.ArticleImageBinding
import com.jayce.vexis.databinding.ParagraphItemLayoutBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.ParaRemarkEntry
import com.jayce.vexis.foundation.route.ArticleService
import com.jayce.vexis.foundation.view.block.FlexibleDialog

class ParaAdapter(
    val context: Context,
    val activity: Activity,
    private val itemList: List<ParaRemarkEntry>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = arrayListOf(
            "表述不清", "内容啰嗦", "语法错误", "逻辑混乱", "前后矛盾", "缺少佐证"
        )

    private var articleId: Long = -1

    private val dialog by lazy {
        FlexibleDialog<AddCommentLayoutBinding>(context, activity.layoutInflater)
            .title("留言")
            .flexibleView(AddCommentLayoutBinding::inflate) {
                singleSelect.setChildLayout(list) {
                    commentContent.hint = it
                }
            }
            .cancelable(true)
    }

    class ViewHolder(val binding: ParagraphItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val paragraph = binding.content
    }

    class ImageViewHolder(val binding: ArticleImageBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.img
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val viewHolder = if (viewType == 1) {
            val binding = ParagraphItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding = ArticleImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolder(binding)
        }

        return viewHolder
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val type = holder.itemViewType
        val item = itemList[position]
        if (type == 1) {
            val currentHolder = holder as ViewHolder
            currentHolder.paragraph.setOnLongClickListener {
                it.setBackgroundColor(context.resources.getColor(R.color.BeanGreen, null))
                showCommentDialog(position, it)
                true
            }
            if (item.list.isEmpty()) {
                currentHolder.paragraph.text = item.content.trim()
                return
            }
            displayComment(position, holder.paragraph)
        } else {
            val currentHolder = holder as ImageViewHolder
            NetTool.setImage(context, currentHolder.image, "${Config.BASE_FILE_PATH}bZuTJX1743912177610.jpg")
        }

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
            title("评论")
            positive("提交") {
                view.setBackgroundColor(context.resources.getColor(R.color.white, null))
                val userId = user().userId
                val paragraphId = itemList[position].paragraphId
                val content = commentContent.msg()
                request<ArticleService, Boolean>({
                    postCommen(articleId, paragraphId, userId, content)
                }) {
                    ui { it.toast() }
                }
                itemList[position].paragraphId.toast()
                return@positive -1
            }
            show()
        }
    }

    fun setArticleId(articleId: Long) {
        this.articleId = articleId
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 ==0) {
            1
        } else {
            2
        }
    }
}