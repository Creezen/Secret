package com.jayce.vexis.writing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.databinding.ParagraphItemBinding
import com.jayce.vexis.writing.paragraph.ParagraphActivity

class ArticleAdapter(
    val context: Context,
    val itemList: List<ArticleBean>,
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    class ViewHolder(val binding: ParagraphItemBinding): RecyclerView.ViewHolder(binding.root) {
        val paragraph = binding.paragraph
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ParagraphItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.paragraph.text = item.title
        holder.paragraph.setOnClickListener {
            context.startActivity(Intent(context, ParagraphActivity::class.java)
                .apply {
                    putExtra("articleId", item.synergyId)
                }
            )
        }
    }

    override fun getItemCount() = itemList.size
}
