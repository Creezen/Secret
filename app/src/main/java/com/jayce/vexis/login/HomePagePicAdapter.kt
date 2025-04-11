package com.jayce.vexis.login

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jayce.vexis.R
import com.jayce.vexis.databinding.HomePagePicLayoutBinding

class HomePagePicAdapter(
    private val context: Context,
    private val list: List<String>
) : RecyclerView.Adapter<HomePagePicAdapter.ViewHolder>(){
    class ViewHolder(val binding: HomePagePicLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomePagePicLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        Glide.with(context)
            .load(item)
            .placeholder(R.drawable.loading)
            .into(holder.imageView)
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = position
}
