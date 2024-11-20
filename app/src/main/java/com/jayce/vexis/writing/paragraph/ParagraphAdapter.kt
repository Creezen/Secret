package com.jayce.vexis.writing.paragraph

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
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.R
import com.jayce.vexis.databinding.AddCommentLayoutBinding
import com.jayce.vexis.databinding.ParagraphItemLayoutBinding
import com.jayce.vexis.widgets.CustomDialog

class ParagraphAdapter(
    val context: Context,
    val activity: Activity,
    private val itemList: List<ParagraphCommandBean>
): RecyclerView.Adapter<ParagraphAdapter.ViewHolder>() {

    private val list = arrayListOf(
        "富强", "民主", "文明", "和谐",
        "自由", "平等", "公正", "法治",
        "爱国", "敬业", "诚信", "友善"
    )

    private val dialog by lazy {
        CustomDialog(
            context,
            AddCommentLayoutBinding.inflate(activity.layoutInflater)
        ).apply {
            setTitle("留言")
            viewBinding.singleSelect.setChildLayout(list)
        }
    }

    class ViewHolder(val binding: ParagraphItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val paragraph = binding.content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ParagraphItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.paragraph.setOnLongClickListener {
            it.setBackgroundColor(context.resources.getColor(R.color.BeanGreen))
            showCommentDialog(position, it)
            true
        }
        if (item.list.isEmpty()) {
            holder.paragraph.text = item.content
            return
        }
        displayComment(position, holder.paragraph)
    }

    override fun getItemCount() = itemList.size

    private fun displayComment(
        position: Int,
        textView: TextView
    ) {
        val contentLength = itemList[position].content.length
        val imageSpan = ImageSpan(context, R.drawable.comment)
        val clickSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                "I am click".toast()
            }
        }
        val spanString = SpannableString("${itemList[position].content}    ")
        spanString.setSpan(imageSpan, contentLength, contentLength + 4, ImageSpan.ALIGN_CENTER)
        spanString.setSpan(clickSpan, contentLength, contentLength + 4, ImageSpan.ALIGN_CENTER)
        textView.text = spanString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showCommentDialog(position: Int, view: View) {
        dialog.apply {
            setCustomLeftButton("取消") { _, dialog ->
                view.setBackgroundColor(context.resources.getColor(R.color.white))
                dismiss()
            }
            setCustomRightButton("提交") { _, dialog ->
                view.setBackgroundColor(context.resources.getColor(R.color.white))
                dismiss()
            }
            show()
        }
    }
}