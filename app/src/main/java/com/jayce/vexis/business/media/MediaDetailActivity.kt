package com.jayce.vexis.business.media

import android.os.Build
import android.os.Bundle
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFileDetailBinding

class MediaDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityFileDetailBinding
    private lateinit var fileItem: MediaItem
    private val parentNode = arrayListOf("资源描述", "资源说明")
    private val childNode = arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf())

    private val mediaDetailAdapter by lazy {
        MediaDetailAdapter(this, parentNode, childNode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fileItem = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("fileInfo", MediaItem::class.java) ?: MediaItem("","","","","",0,"")
        } else {
            intent.getParcelableExtra("fileInfo") ?: MediaItem("","","","","",0,"")
        }
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
            expView.setAdapter(mediaDetailAdapter)
            expView.expandGroup(0)
            expView.expandGroup(1)
        }
    }

    private fun initData() {
        childNode[0].add(fileItem.description)
        childNode[1].add(fileItem.illustrate)
        mediaDetailAdapter.notifyDataSetChanged()
    }
}