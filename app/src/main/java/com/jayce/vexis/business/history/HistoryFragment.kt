package com.jayce.vexis.business.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.registerSwipeEvent
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.unregisterSwipeEvent
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.DialogTimelineBinding
import com.jayce.vexis.databinding.TimeLineBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.foundation.bean.HistoryEntry
import com.jayce.vexis.foundation.route.HistoryService
import com.jayce.vexis.foundation.view.block.CustomDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryFragment : BaseFragment<BaseViewModel>(), SwipeCallback {

    companion object {
        const val TAG = "HistoryFragment"
    }

    private lateinit var binding: TimeLineBinding
    private val eventHandle by lazy {
        ClickHandle(ClickHandle.Mode.LISTENER)
    }

    private var ratio: Float = -1f
    private var rootWidth: Int = -1
    private var rootHeight: Int = -1

    private val eventList: ArrayList<HistoryEntry> = arrayListOf()

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

    override fun onResume() {
        super.onResume()
        queryList()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            queryList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.base.unregisterSwipeEvent("base", eventHandle)
    }

    private fun queryList() {
        request<HistoryService, List<HistoryEntry>>({ queryAllEvent() }) {
            eventList.addAll(it)
            binding.left.addTraceCell(
                it.filter { it.isValid() }
            )
        }
    }

    private fun initView() {
        with(binding) {
            base.registerSwipeEvent("base", eventHandle, this@HistoryFragment)
            left.init(0, 2524608000000) {
                it.message.toast()
            }
            floatingBtn.setOnClickListener {
                CustomDialog(
                    requireContext(),
                    DialogTimelineBinding.inflate(layoutInflater)
                ).apply {
                    right { binding, dialog ->
                        request<HistoryService, Boolean> ({ sendEventData(
                            binding.picker.formatTime(),
                            binding.content.msg()
                        )}){
                            withContext(Dispatchers.Main) {
                                it.toast()
                            }
                        }
                        dismiss()
                    }
                    left { _, _ -> dismiss() }
                    show()
                }
            }
        }
    }

    override fun onPinchIn(
        viewId: String,
        scaleFactor: Float,
    ): Boolean {
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