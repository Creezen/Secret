package com.jayce.vexis.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.registerSwipeEvent
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.TimeLineBinding

class HistoricalAxis : BaseFragment(), SwipeCallback {

    companion object {
        const val TAG = "HistoricalAxis"
    }
    private lateinit var binding: TimeLineBinding
    private val eventHandle by lazy {
        ClickHandle(ClickHandle.Mode.LISTENER)
    }

    private var ratio: Float = -1f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TimeLineBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            base.registerSwipeEvent("base", eventHandle, this@HistoricalAxis)
        }
    }

    override fun onPinchIn(viewId: String, scaleFactor: Float): Boolean {
        Log.d(TAG,"onPinchIn  $viewId  $scaleFactor")
        if(viewId == "base") {
            val param = binding.center.layoutParams
            val measuredWidth = binding.center.measuredWidth
            val measuredHeight = binding.center.measuredHeight
            if(ratio < 0) {
                ratio = measuredWidth.toFloat() / measuredHeight.toFloat()
            }
            param.height = (measuredHeight.toFloat() * scaleFactor).toInt()
            param.width = (param.height.toFloat() * ratio).toInt()
            if (param.height <= 20 || param.width <= 20) return true
            binding.center.layoutParams = param
        }
        return super.onPinchIn(viewId, scaleFactor)
    }

    override fun onPinchOut(viewId: String, scaleFactor: Float): Boolean {
        Log.d(TAG,"onPinchOut   $viewId  $scaleFactor")
        if(viewId == "base") {
            val param = binding.center.layoutParams
            val measuredWidth = binding.center.measuredWidth
            val measuredHeight = binding.center.measuredHeight
            if(ratio < 0) {
                ratio = measuredWidth.toFloat() / measuredHeight.toFloat()
            }
            param.height = (measuredHeight.toFloat() * scaleFactor).toInt()
            param.width = (param.height.toFloat() * ratio).toInt()
            binding.center.layoutParams = param
        }
        return super.onPinchOut(viewId, scaleFactor)
    }
}