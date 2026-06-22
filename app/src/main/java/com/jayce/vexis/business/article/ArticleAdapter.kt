package com.jayce.vexis.business.article

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jayce.vexis.util.bean.ArticleBean
import com.jayce.vexis.business.article.section.SectionActivity
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.ParagraphItemBinding
import com.jayce.vexis.foundation.ui.CardAdapter

class ArticleAdapter(
    val context: Context,
    private var itemList: List<ArticleBean>,
) : CardAdapter<ArticleBean, ParagraphItemBinding, ArticleAdapter.ViewHolder>(itemList) {

    class ViewHolder(container: CardItemLayoutBinding, binding: ParagraphItemBinding) : CardAdapter.ViewHolder(container) {
        val paragraph = binding.paragraph
    }

    override fun getItemCount() = itemList.size

    override fun getAttachedList() = itemList

    override fun updateAttachedList(newList: List<ArticleBean>) {
        itemList = newList
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.paragraph.text = item.title
        val intent = Intent(context, SectionActivity::class.java).apply {
            putExtra("articleId", item.articleId)
        }
        holder.paragraph.setOnClickListener {
            context.startActivity(intent)
        }
    }

    override fun getChildAndHoler(
        viewType: Int,
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<ParagraphItemBinding, ViewHolder> {
        val child = ParagraphItemBinding.inflate(layoutInflater, parent, false)
        val holder = ViewHolder(containerBinding, child)
        return child to holder
    }
}