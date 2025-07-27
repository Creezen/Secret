package com.jayce.vexis.business.media

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.jayce.vexis.foundation.base.BaseFragment
import com.jayce.vexis.databinding.FileShareBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MediaLibraryActivity : BaseFragment() {
    private lateinit var binding: FileShareBinding
    private var readExternalLaunch: ActivityResultLauncher<Intent>? = null
    private val resItemList = ArrayList<MediaItem>()
    private val adapter: MediaElementAdapter by lazy {
        MediaElementAdapter(requireActivity(), requireActivity(), resItemList)
    }
    private var tag: Any? = null

    override fun registerLauncher() {
        readExternalLaunch =
            getLauncher(startActivity()) {
                Log.e("MediaLibraryActivity.registerLauncher", "$it")
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
        kotlin.runCatching {
            lifecycleScope.launch(Dispatchers.IO) {
                val result =
                    NetTool.create<MediaService>()
                        .fetchFile()
                        .await()
                val list = result["items"]
                if (list.isNullOrEmpty()) {
                    return@launch
                }
                resItemList.clear()
                resItemList.addAll(list)
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }.onFailure {
            Log.e("MediaLibraryActivity.initData", "$it")
        }
    }

    private fun initPage() {
        with(binding) {
            uploadFile.setOnClickListener {
                if (Environment.isExternalStorageManager()) {
                    startActivity(Intent(this@MediaLibraryActivity.activity, MediaUploadActivity::class.java))
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