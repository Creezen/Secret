package com.jayce.vexis.business.file.pool

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
import com.creezen.commontool.bean.FileBean
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FileShareBinding
import com.jayce.vexis.domain.route.FileService
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.foundation.Util.request

class FileContentsFragment(
    private val viewModel: FileViewModel
) : BaseFragment<FileShareBinding>() {

    private var readExternalLaunch: ActivityResultLauncher<Intent>? = null
    private val fileItemList = ArrayList<FileBean>()
    private val adapter: FileEntryAdapter by lazy {
        FileEntryAdapter(requireActivity(), fileItemList, viewModel)
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

    fun updateData() {
        initData()
    }

    private fun initData() {
        request<FileService, List<FileBean>>({ fetchFile() }) {
            adapter.notifyDataChange(it)
        }
    }

    private fun initPage() = binding.apply {
        uploadFile.setOnClickListener {
            if (Environment.isExternalStorageManager()) {
                startActivity(Intent(this@FileContentsFragment.activity, FileUploadActivity::class.java))
            } else {
                readExternalLaunch?.launch(
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                        it.data = Uri.parse("package:" + activity?.packageName)
                    },
                )
            }
        }
        refresh.setOnClickListener {
            initData()
        }
        recycle.layoutManager = LinearLayoutManager(context)
        recycle.adapter = adapter
    }
}