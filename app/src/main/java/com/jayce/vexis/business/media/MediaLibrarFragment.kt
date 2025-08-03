package com.jayce.vexis.business.media

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.databinding.FileShareBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.base.BaseFragment
import com.jayce.vexis.foundation.base.BaseViewModel
import com.jayce.vexis.foundation.bean.MediaEntry
import com.jayce.vexis.foundation.route.MediaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaLibrarFragment : BaseFragment<BaseViewModel>() {

    private lateinit var binding: FileShareBinding
    private var readExternalLaunch: ActivityResultLauncher<Intent>? = null
    private val resItemList = ArrayList<MediaEntry>()
    private val adapter: MediaElementAdapter by lazy {
        MediaElementAdapter(requireActivity(), requireActivity(), resItemList)
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
        binding = FileShareBinding.inflate(inflater)
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
        request<MediaService, LinkedHashMap<String, List<MediaEntry>>>({ fetchFile() }) {
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
                    startActivity(Intent(this@MediaLibrarFragment.activity, MediaUploadActivity::class.java))
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