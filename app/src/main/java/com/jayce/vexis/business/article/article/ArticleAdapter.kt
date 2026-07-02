package com.jayce.vexis.business.article.article

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jayce.vexis.R
import com.jayce.vexis.business.article.section.SectionActivity
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.ParagraphItemBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ability.menu.MenuManager.registerOnMenuClick
import com.jayce.vexis.foundation.ability.menu.OnMenuClick
import com.jayce.vexis.foundation.ui.CardAdapter
import com.jayce.vexis.util.bean.ArticleBean

class ArticleAdapter(
    val context: Context,
    private var itemList: List<ArticleBean>,
) : CardAdapter<ArticleBean, ParagraphItemBinding, ArticleAdapter.ViewHolder>(itemList), OnMenuClick {

    private var onDelete: (() -> Unit)? = null

    fun setOnDelete(callBack: () -> Unit) {
        this.onDelete = callBack
    }

    class ViewHolder(container: CardItemLayoutBinding, binding: ParagraphItemBinding) : CardAdapter.ViewHolder(container) {
        val paragraph = binding.paragraph
        val view = binding.article
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
            putExtra("articleTitle", item.title)
        }
        holder.view.setOnClickListener {
            context.startActivity(intent)
        }
        val bundle = Bundle().apply { putLong("articleId", item.articleId) }
        holder.view.registerOnMenuClick(context, R.menu.article_action, bundle, this)
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

    override fun onMenuItemClicked(menuId: Int, bundle: Bundle?) {
        val articleId = bundle?.getLong("articleId") ?: return
        if (menuId != R.id.deleteArticle) return
        request<ArticleService, _>({ deleteArticle(articleId) }) {
            it.onTrue {
                onDelete?.invoke()
            }
        }
    }
}