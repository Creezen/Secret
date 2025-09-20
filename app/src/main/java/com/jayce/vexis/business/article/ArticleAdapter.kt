package com.jayce.vexis.business.article

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.commontool.bean.ArticleBean
import com.jayce.vexis.business.article.paragraph.ParagraptActivity
import com.jayce.vexis.databinding.ParagraphItemBinding

class ArticleAdapter(
    val context: Context,
    val itemList: List<ArticleBean>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(val binding: ParagraphItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val paragraph = binding.paragraph
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val binding = ParagraphItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val item = itemList[position]
        val currentHolder = holder as ViewHolder
        currentHolder.paragraph.text = item.title
        currentHolder.paragraph.setOnClickListener {
            context.startActivity(
                Intent(context, ParagraptActivity::class.java)
                    .apply {
                        putExtra("articleId", item.articleId)
                    },
            )
        }
    }

    override fun getItemCount() = itemList.size
}