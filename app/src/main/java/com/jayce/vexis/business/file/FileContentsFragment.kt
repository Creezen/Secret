package com.jayce.vexis.business.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.FileBean
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FileShareBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.FileService
import java.util.ArrayList
import java.util.LinkedHashMap

class FileContentsFragment : BaseFragment<FileShareBinding>() {

    private var readExternalLaunch: ActivityResultLauncher<Intent>? = null
    private val resItemList = ArrayList<FileBean>()
    private val adapter: FileEntryAdapter by lazy {
        FileEntryAdapter(requireActivity(), requireActivity(), resItemList)
    }
    private var tag: Any? = null

    override fun registerLauncher() {
        readExternalLaunch = getLauncher(startActivity()) {
                Log.e("MediaLibrarFragment.registerLauncher", "$it")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (tag != null) {
            initData()
            return tag as View
        }
        initPage()
        initData()
        return binding.root.also {
            tag = it
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        request<FileService, LinkedHashMap<String, List<FileBean>>>({ fetchFile() }) {
            val list = it["items"]
            if (list.isNullOrEmpty()) {
                return@request
            }
            resItemList.clear()
            resItemList.addAll(list)
            ui { adapter.notifyDataSetChanged() }
        }
    }

    private fun initPage() {
        with(binding) {
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
            adapter.setProgressBar(progress)
            recycle.adapter = adapter
        }
    }
}