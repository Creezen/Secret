package com.jayce.vexis.business.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.creezen.tool.AndroidTool.toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.business.history.api.OnOptionClickListener
import com.jayce.vexis.business.history.api.OnViewReady
import com.jayce.vexis.business.history.panel.FindTimeFragment
import com.jayce.vexis.business.history.panel.ScaleTimeFragment
import com.jayce.vexis.business.history.panel.UpdateTimeFragment
import com.jayce.vexis.databinding.HistoryOptionPanelBinding
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.foundation.ui.block.TabLayoutTitle

class HistoryOptionPanel(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet), OnTabSelectedListener, OnViewReady {

    private val activity = context as FragmentActivity
    private val owner = context as LifecycleOwner
    private val fragments = arrayListOf<Fragment>()
    private val historyPanelAdapter = HistoryPanelAdapter(activity.supportFragmentManager, owner.lifecycle, fragments)

    private val findTimeFragment = FindTimeFragment()
    private val updateTimeFragment = UpdateTimeFragment()
    private val scaleTimeFragment = ScaleTimeFragment()

    private var listener: OnOptionClickListener? = null
    private var selectPosition: Int = 0

    private val binding = HistoryOptionPanelBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        initFragment()
        initView()
    }

    fun addOnOptionClickListener(onOptionClickListener: OnOptionClickListener) {
        listener = onOptionClickListener
    }

    private fun initFragment() {
        scaleTimeFragment.setOnViewReadyListener(this)
        updateTimeFragment.setOnViewReadyListener(this)
        findTimeFragment.setOnViewReadyListener(this)
        fragments.add(scaleTimeFragment)
        fragments.add(updateTimeFragment)
        fragments.add(findTimeFragment)
    }

    private fun initView() = binding.apply {
        page.adapter = historyPanelAdapter
        TabLayoutMediator(tab, page) { tab, pos ->
            val title = TabLayoutTitle(context)
            title.text = when (pos) {
                0 -> "缩放"
                1 -> "设置"
                2 -> "跳转"
                else -> ""
            }
            tab.customView = title
        }.attach()
        tab.addOnTabSelectedListener(this@HistoryOptionPanel)
        button.text = "调整比例"
        button.setOnClickListener {
            when (selectPosition) {
                0 -> {
                    listener?.onScaleChange(scaleTimeFragment.scale)
                    "调整比例 ${scaleTimeFragment.scale}".toast()
                }
                1 -> {
                    val start = updateTimeFragment.updatedStartTime
                    val end = updateTimeFragment.updatedEndTime
                    listener?.onTimeChange(start, end)
                    "设置时间".toast()
                }
                2 -> {
                    listener?.onSearch(0, "", TimeUnitEntry.zero())
                "搜索跳转".toast()
                }
                else -> "".toast()
            }
            visibility = GONE
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) { /**/ }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab == null) return
        selectPosition = tab.position
        binding.button.text = when (tab.position) {
            0 -> "调整比例"
            1 -> "设置时间"
            2 -> "搜索跳转"
            else -> ""
        }

        val view = fragments[selectPosition].view ?: return
        adjustTabSize(view)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) { /**/ }

    override fun onReady(view: View) {
        adjustTabSize(view)
    }

    private fun adjustTabSize(view: View) {
        val viewPage = binding.page
        val widthMeasure = MeasureSpec.makeMeasureSpec(viewPage.width, MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(widthMeasure, heightMeasure)

        val param = viewPage.layoutParams
        if (param.height != view.measuredHeight) param.height = view.measuredHeight
        viewPage.layoutParams = param
    }
}