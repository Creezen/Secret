package com.jayce.vexis.hub.resource

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.jayce.vexis.BaseActivity
import com.jayce.vexis.databinding.ActivityFileDetailBinding

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class FileDetailActivity: BaseActivity() {

    private lateinit var binding: ActivityFileDetailBinding
    private lateinit var fileItem: ResourceItem
    private val parentNode by lazy {
        arrayListOf("资源描述", "资源说明")
    }
    private val childNode by lazy {
        arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf())
    }
    private val fileDetailAdapter by lazy {
        FileDetailAdapter(this, parentNode, childNode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileItem = intent.getParcelableExtra("fileInfo", ResourceItem::class.java) ?: return
        Log.e("FileDetailActivity.onCreate","$fileItem")
        initView()
        initData()
    }

    private fun initView() {
        with(binding) {
            name.text = fileItem.fileName
            fileID.text = fileItem.fileID
            date.text = fileItem.uploadTime
            size.text = "${fileItem.fileSize}"
            expView.setAdapter(fileDetailAdapter)
        }
    }

    private fun initData() {
        childNode[0].add(fileItem.description)
        childNode[1].add(fileItem.illustrate)
        fileDetailAdapter.notifyDataSetChanged()
    }
}