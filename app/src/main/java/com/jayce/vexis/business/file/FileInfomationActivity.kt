package com.jayce.vexis.business.file

import android.os.Build
import android.os.Bundle
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.commontool.bean.FileBean
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFileDetailBinding
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.foundation.bean.FileEntry

class FileInfomationActivity : BaseActivity<ActivityFileDetailBinding>() {

    private lateinit var fileItem: FileBean
    private val parentNode = arrayListOf("资源描述", "资源说明")
    private val childNode = arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf())

    private val fileInformationAdapter by lazy {
        FileInformationAdapter(this, parentNode, childNode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("fileInfo", FileEntry::class.java) ?: FileEntry(EMPTY_STRING,EMPTY_STRING,EMPTY_STRING, EMPTY_STRING, EMPTY_STRING,0,EMPTY_STRING)
        } else {
            intent.getParcelableExtra("fileInfo") ?: FileEntry(EMPTY_STRING,EMPTY_STRING,EMPTY_STRING,EMPTY_STRING,EMPTY_STRING,0,EMPTY_STRING)
        }
        fileItem = item.unParcelable()
        initView()
        initData()
    }

    private fun initView() {
        with(binding) {
            name.text = fileItem.fileName
            name.isSelected = true
            fileID.text = fileItem.fileID
            date.text = fileItem.uploadTime
            size.text = "${fileItem.fileSize}"
            expView.setAdapter(fileInformationAdapter)
            expView.expandGroup(0)
            expView.expandGroup(1)
        }
    }

    private fun initData() {
        childNode[0].add(fileItem.description)
        childNode[1].add(fileItem.illustrate)
        fileInformationAdapter.notifyDataSetChanged()
    }
}