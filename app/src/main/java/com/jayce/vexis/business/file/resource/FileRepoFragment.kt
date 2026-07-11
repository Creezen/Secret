package com.jayce.vexis.business.file.resource

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.util.bean.FileBean
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FileShareBinding
import com.jayce.vexis.domain.route.FileService
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.foundation.Util.request

class FileRepoFragment(
    private val viewModel: FileViewModel
) : BaseFragment<FileShareBinding>() {

    private var readExternalLaunch: ActivityResultLauncher<Intent>? = null
    private val fileItemList = ArrayList<FileBean>()
    private val adapter: FileRepoAdapter by lazy {
        FileRepoAdapter(requireActivity(), fileItemList, viewModel)
    }
    private var tag: Any? = null

    override fun registerLauncher() {
        readExternalLaunch = getLauncher(startActivity()) { /**/ }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (tag != null) {
            return tag as View
        }
        initPage()
        return binding.root.also {
            tag = it
        }
    }

    fun updateData() { initData() }

    private fun initData() {
        request<FileService, List<FileBean>>({ fetchFile() }) {
            adapter.notifyDataChange(it)
        }
    }

    private fun initPage() = binding.apply {
        uploadFile.setOnClickListener {
            if (Environment.isExternalStorageManager()) {
                startActivity(Intent(this@FileRepoFragment.activity, FileUploadActivity::class.java))
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + activity?.packageName)
                readExternalLaunch?.launch(intent)
            }
        }
        refresh.setOnClickListener { initData() }
        recycle.layoutManager = LinearLayoutManager(context)
        recycle.adapter = adapter
    }
}