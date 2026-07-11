package com.jayce.vexis.business.file.resource

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.jayce.vexis.R
import com.jayce.vexis.databinding.ChildNodeLayoutBinding
import com.jayce.vexis.databinding.ParentNodeLayoutBinding

class FileMetaAdapter(
    private val context: Context,
    private val parentList: List<String>,
    private val childList: List<List<String>>,
) : BaseExpandableListAdapter() {

    override fun getGroupCount() = parentList.size

    override fun getChildrenCount(groupPosition: Int) = childList[groupPosition].size

    override fun getGroup(groupPosition: Int) = parentList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int) = childList[groupPosition][childPosition]

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun hasStableIds() = true

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val binding: ParentNodeLayoutBinding
        var view = convertView
        if (view != null) {
            binding = view.tag as ParentNodeLayoutBinding
        } else {
            binding = ParentNodeLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        }
        val colorId = if (isExpanded) R.color.transparent else R.color.LightApricot
        binding.parentNodeName.setBackgroundResource(colorId)
        binding.parentNodeName.text = parentList[groupPosition]
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val binding: ChildNodeLayoutBinding
        var view = convertView
        if (view != null) {
            binding = view.tag as ChildNodeLayoutBinding
        } else {
            binding = ChildNodeLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        }
        val text = if (childList[groupPosition][childPosition].isEmpty()) {
            "无"
        } else {
            childList[groupPosition][childPosition]
        }
        binding.childNodeName.text = text
        return view
    }
}