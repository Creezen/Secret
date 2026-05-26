package com.jayce.vexis.business.file.pool

import android.os.Bundle
import com.creezen.commontool.Config.NIL
import com.creezen.commontool.bean.FileBean
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFileDetailBinding
import com.jayce.vexis.domain.bean.FileEntry
import com.jayce.vexis.foundation.Util.Extension.unParcelable

class FileInformationActivity : BaseActivity<ActivityFileDetailBinding>() {

    private lateinit var fileItem: FileBean
    private val parentNode = arrayListOf("资源描述", "资源说明")
    private val childNode = arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf())

    private val fileInformationAdapter by lazy {
        FileInformationAdapter(this, parentNode, childNode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.getParcelableExtra("fileInfo", FileEntry::class.java) ?: FileEntry(NIL, NIL, NIL, NIL, NIL,0, NIL)
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
        expView.setAdapter(fileInformationAdapter)
        expView.expandGroup(0)
        expView.expandGroup(1)
    }

    private fun initData() {
        childNode[0].add(fileItem.description)
        childNode[1].add(fileItem.illustrate)
        fileInformationAdapter.notifyDataSetChanged()
    }
}