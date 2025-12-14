package com.jayce.vexis.business.file

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.creezen.commontool.Config.Constant.NUM_2
import com.creezen.commontool.Config.Constant.NUM_4
import com.creezen.tool.ThreadTool
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.R
import com.jayce.vexis.business.file.pool.FileContentsFragment
import com.jayce.vexis.business.file.submodule.DynamicModuleFragment
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FileFragmentLayoutBinding
import com.jayce.vexis.foundation.view.block.DownloadButtonSheetDialog
import com.jayce.vexis.foundation.viewmodel.FileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FileFragment : BaseFragment<FileFragmentLayoutBinding>() {

    private val list = arrayListOf<Fragment>()
    private var fileAdapter: FileAdapter? = null

    private val viewModel by inject<FileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        initData()
        initView()
        processDownload()
        return binding.root
    }

    private fun initData() {
        val fileContentsFragment = FileContentsFragment(viewModel)
        val dynamicModuleFragment = DynamicModuleFragment(viewModel)
        list.add(fileContentsFragment)
        list.add(dynamicModuleFragment)
        viewModel.fetchAndHandleTask()
    }

    private fun initView() {
        if (fileAdapter == null) {
            val manager =  if (isAdded) parentFragmentManager else null
            if (manager == null) return
            fileAdapter = FileAdapter(manager, lifecycle, list)
        }

        binding.apply {
            page.adapter = fileAdapter
            TabLayoutMediator(tab, page) { tab, pos ->
                val textView = TextView(this@FileFragment.context)
                textView.gravity = Gravity.CENTER
                textView.text = when (pos) {
                    0 -> "资源共享"
                    1 -> "随用随下"
                    else -> ""
                }
                tab.customView = textView
            }.attach()

            progress.setOnClickListener { bottomSheetDialog ->
                val dialog = DownloadButtonSheetDialog {
                    val view = findViewById<View>(R.id.sheet_root) ?: return@DownloadButtonSheetDialog
                    val paraa = resources.displayMetrics.heightPixels * 0.4
                    view.layoutParams.height = paraa.toInt()

                    val behavior = BottomSheetBehavior.from(view.parent as View)
                }

                dialog.onDismiss {
                    progress.visibility = View.VISIBLE
                }
                dialog.show(parentFragmentManager, "download")
                progress.visibility = View.GONE
            }
        }
    }

    private fun processDownload() {
        ThreadTool.runOnMulti {
            lifecycleScope.launch {
                viewModel.taskStateFlow.collect {
                    binding.progress.max = it.size.toInt()
                    binding.taskName.text = getSpannerString(it.fileName, it.taskLastCount)
                    if (it.size < 0) {
                        binding.fileSize.visibility = View.GONE
                    } else {
                        binding.fileSize.visibility = View.VISIBLE
                        binding.fileSize.text = viewModel.handleSizeDisplay(it.size)
                    }
                }
            }

            lifecycleScope.launch {
                viewModel.progressFlow.collect {
                    binding.progress.progress = it
                }
            }
        }
    }

    private fun getSpannerString(content: String, count: Int): SpannableString {
        val showString = "$content  (${count})"
        val span = ForegroundColorSpan(Color.GRAY)
        val spanString = SpannableString(showString)
        val originLength = content.length
        val realLength = showString.length
        spanString.setSpan(span, originLength, realLength, ImageSpan.ALIGN_CENTER)
        return spanString
    }
}