package com.jayce.vexis.business.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.commontool.bean.HistoryBean
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.registerSwipeEvent
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.unregisterSwipeEvent
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.DialogTimelineBinding
import com.jayce.vexis.databinding.FragmentHistoryBinding
import com.jayce.vexis.domain.route.HistoryService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.FlexibleDialog

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(), SwipeCallback {

    private val eventHandle = ClickHandle(ClickHandle.Mode.LISTENER)

    private var ratio: Float = -1f
    private var rootWidth: Int = -1
    private var rootHeight: Int = -1

    private val eventList: ArrayList<HistoryBean> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        updateSize()
        return binding.root
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        queryList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.base.unregisterSwipeEvent("base", eventHandle)
    }

    fun changeOptionPanel() {
        val panel = binding.optionPanel
        val visibility = panel.visibility
        panel.visibility = when (visibility) {
            View.GONE -> View.VISIBLE
            View.VISIBLE -> View.GONE
            else -> View.VISIBLE
        }
    }

    private fun updateSize() {
        binding.root.post {
            rootHeight = binding.root.measuredHeight
            rootWidth = binding.root.width
        }
    }

    private fun queryList() {
        request<HistoryService, List<HistoryBean>>({ queryAllEvent() }) {
            eventList.addAll(it)
            binding.left.addTraceCell(it.filter { it.isValid() })
        }
    }

    private fun initView() = binding.apply {
        base.registerSwipeEvent("base", eventHandle, this@HistoryFragment)
        left.init(0, 2524608000000) {
            it.message.toast()
        }
        floatingBtn.setOnClickListener {
            val ctx = activity ?: return@setOnClickListener
            FlexibleDialog<DialogTimelineBinding>(ctx)
                .flexibleView(DialogTimelineBinding::inflate)
                .positive {
                    val time = picker.formatTime()
                    val msg = content.msg()
                    request<HistoryService, _>({ sendEventData(time, msg) }) { it.toast() }
                }
                .show()
        }

        scroll.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//            Log.d("LJW", "scrollY: $scrollY  oldScrollY: $oldScrollY")
        }
    }

    override fun onPinchIn(viewId: String, scaleFactor: Float): Boolean {
        if (viewId == "base") {
            val param = binding.center.layoutParams
            val measuredHeight = binding.center.height
            param.height = (measuredHeight.toFloat() * scaleFactor).toInt()
            if (param.height <= rootHeight) param.height = rootHeight
            binding.center.layoutParams = param
        }
        return super.onPinchIn(viewId, scaleFactor)
    }

    override fun onPinchOut(viewId: String, scaleFactor: Float): Boolean {
        if (viewId == "base") {
            val param = binding.center.layoutParams
            val measuredWidth = binding.center.measuredWidth
            val measuredHeight = binding.center.measuredHeight
            if (ratio < 0) {
                ratio = measuredWidth.toFloat() / measuredHeight.toFloat()
            }
            param.height = (measuredHeight.toFloat() * scaleFactor).toInt()
            binding.center.layoutParams = param
        }
        return super.onPinchOut(viewId, scaleFactor)
    }
}