package com.jayce.vexis.business.file.resource

import android.os.Bundle
import android.util.TypedValue
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFileDetailBinding
import com.jayce.vexis.domain.bean.FileEntry
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.bean.FileBean
import kotlin.math.floor

class FileMetaActivity : BaseActivity<ActivityFileDetailBinding>() {

    private lateinit var fileItem: FileBean
    private val parentNode = arrayListOf("资源描述", "资源说明")
    private val childNode = arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf())
    private val fileMetaAdapter = FileMetaAdapter(this, parentNode, childNode)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.getParcelableExtra("fileInfo", FileEntry::class.java) ?: FileEntry(NIL, NIL, NIL, NIL, NIL, NIL,0, NIL)
        fileItem = item.unParcelable()
        initView()
        initData()
    }

    private fun initView() = binding.apply {
        name.text = fileItem.fileName
        name.isSelected = true
        fileID.text = fileItem.fileID
        date.text = fileItem.uploadTime
        size.text = "${fileItem.fileSize}"
        expView.setAdapter(fileMetaAdapter)
        expView.expandGroup(0)
        expView.expandGroup(1)

        name.post {
            val paint = name.paint
            val originSize = paint.measureText(name.msg())
            val realWidth = name.width - name.paddingStart - name.paddingEnd
            val scale = realWidth / originSize
            val realSize = name.textSize * scale * 0.98f
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, floor(realSize))
        }
        expView.setOnGroupExpandListener {
            fileMetaAdapter.notifyDataSetChanged()
        }
        expView.setOnGroupCollapseListener { fileMetaAdapter.notifyDataSetChanged() }
    }

    private fun initData() {
        childNode[0].add(fileItem.description)
        childNode[1].add(fileItem.illustrate)
        fileMetaAdapter.notifyDataSetChanged()
    }
}