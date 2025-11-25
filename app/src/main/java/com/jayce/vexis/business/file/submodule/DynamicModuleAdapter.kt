package com.jayce.vexis.business.file.submodule

import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.databinding.DynamicModuleItemLayoutBinding
import com.jayce.vexis.foundation.bean.DynamicModuleEntry

class DynamicModuleAdapter(val list: List<DynamicModuleEntry>) : RecyclerView.Adapter<DynamicModuleAdapter.ViewHolder>() {

    private val effect = RenderEffect.createBlurEffect(15f, 15f, Shader.TileMode.MIRROR)

    class ViewHolder(val binding: DynamicModuleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.itemRoot
        val image = binding.image
        val title = binding.title
        val download = binding.download
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = DynamicModuleItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = list[position]
        holder.title.text = item.title
//        holder.image.setImageURI(Uri.parse(item.image))
        holder.download.setOnClickListener{
            "开始下载模块".toast()
        }
        holder.image.setRenderEffect(effect)
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = position
}