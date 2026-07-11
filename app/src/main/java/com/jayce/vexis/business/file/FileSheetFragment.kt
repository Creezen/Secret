package com.jayce.vexis.business.file

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.R
import com.jayce.vexis.business.file.module.DynamicModuleFragment
import com.jayce.vexis.business.file.resource.FileRepoFragment
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FileFragmentLayoutBinding
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.foundation.ui.block.DownloadButtonSheetDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FileSheetFragment : BaseFragment<FileFragmentLayoutBinding>() {

    private var selectedPosition: Int = 0
    private val list = arrayListOf<Fragment>()
    private var fileSheetAdapter: FileSheetAdapter? = null
    private lateinit var fileRepoFragment: FileRepoFragment
    private lateinit var dynamicModuleFragment: DynamicModuleFragment

    private val viewModel by viewModel<FileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        initData()
        initView()
        processDownload()
        return binding.root
    }

    private fun initData() {
        fileRepoFragment = FileRepoFragment(viewModel)
        dynamicModuleFragment = DynamicModuleFragment(viewModel)
        list.add(fileRepoFragment)
        list.add(dynamicModuleFragment)
        viewModel.startListen()
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        if (firstInit) return
        when (selectedPosition) {
            0 -> fileRepoFragment.updateData()
            1 -> dynamicModuleFragment.updateData()
        }
    }

    private fun initView() {
        if (fileSheetAdapter == null) {
            val manager = if (isAdded) parentFragmentManager else null
            if (manager == null) return
            fileSheetAdapter = FileSheetAdapter(manager, lifecycle, list)
        }

        binding.apply {
            page.adapter = fileSheetAdapter
            TabLayoutMediator(tab, page) { tab, pos ->
                val textView = TextView(this@FileSheetFragment.context)
                textView.gravity = Gravity.CENTER
                textView.text = when (pos) {
                    0 -> "资源共享"
                    1 -> "随用随下"
                    else -> ""
                }
                tab.customView = textView
            }.attach()

            page.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> fileRepoFragment.updateData()
                        1 -> dynamicModuleFragment.updateData()
                    }
                }
            })

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
        lifecycleScope.launch {
            viewModel.taskStateFlow.collect {
                binding.progress.max = it.size.toInt()
                binding.taskName.text = getSpannerString(it.resourceName, viewModel.handleSizeDisplay(it.size))
                if (it.size < 0) {
                    binding.taskCount.visibility = View.GONE
                    binding.taskName.text = it.resourceName
                } else {
                    binding.taskCount.visibility = View.VISIBLE
                    binding.taskCount.text = "余: ${it.taskLastCount}"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.progressFlow.collect {
                binding.progress.progress = it
            }
        }

        lifecycleScope.launch {
            viewModel.taskCountFlow.collect {
                binding.taskCount.text = "余: $it"
            }
        }
    }

    private fun getSpannerString(content: String, sizeString: String): SpannableString {
        val showString = "$content  ( $sizeString )"
        val span = ForegroundColorSpan(Color.GRAY)
        val spanString = SpannableString(showString)
        val originLength = content.length
        val realLength = showString.length
        spanString.setSpan(span, originLength, realLength, ImageSpan.ALIGN_CENTER)
        return spanString
    }
}