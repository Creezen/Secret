package com.jayce.vexis.business.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.registerSwipeEvent
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.unregisterSwipeEvent
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback
import com.jayce.vexis.foundation.base.BaseFragment
import com.jayce.vexis.databinding.DialogTimelineBinding
import com.jayce.vexis.databinding.TimeLineBinding
import com.jayce.vexis.foundation.CustomDialog

class History : BaseFragment(), SwipeCallback {
    companion object {
        const val TAG = "History"
    }

    private lateinit var binding: TimeLineBinding
    private val eventHandle by lazy {
        ClickHandle(ClickHandle.Mode.LISTENER)
    }

    private var ratio: Float = -1f
    private var rootWidth: Int = -1
    private var rootHeight: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = TimeLineBinding.inflate(inflater)
        initView()
        binding.root.apply {
            post {
                rootHeight = measuredHeight
                rootWidth = width
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.base.unregisterSwipeEvent("base", eventHandle)
    }

    private fun initView() {
        with(binding) {
            base.registerSwipeEvent("base", eventHandle, this@History)
            left.init(0, 2524608000000) {
                it.timeStamp.toTime().toast()
            }
            left.addTraceCell(System.currentTimeMillis())
            left.addTraceCell(System.currentTimeMillis() - 30 * 31536000000)
            floatingBtn.setOnClickListener {
                CustomDialog(
                    requireContext(),
                    DialogTimelineBinding.inflate(layoutInflater)
                ).apply {
                    RightButton { dialogTimelineBinding, dialog ->
                        "insert OK".toast()
                        dismiss()
                    }
                    LeftButton { dialogTimelineBinding, dialog ->
                        "insert error".toast()
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    override fun onPinchIn(
        viewId: String,
        scaleFactor: Float,
    ): Boolean {
        Log.d(com.jayce.vexis.business.history.History.Companion.TAG, "onPinchIn  $viewId  $scaleFactor")
        if (viewId == "base") {
            val param = binding.center.layoutParams
            val measuredHeight = binding.center.height
            param.height = (measuredHeight.toFloat() * scaleFactor).toInt()
            if (param.height <= rootHeight) param.height = rootHeight
            binding.center.layoutParams = param
        }
        return super.onPinchIn(viewId, scaleFactor)
    }

    override fun onPinchOut(
        viewId: String,
        scaleFactor: Float,
    ): Boolean {
        Log.d(com.jayce.vexis.business.history.History.Companion.TAG, "onPinchOut   $viewId  $scaleFactor")
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
