package com.jayce.vexis.business.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.commontool.bean.HistoryBean
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.unregisterSwipeEvent
import com.creezen.tool.ThreadTool
import com.creezen.tool.ability.click.ClickHandle
import com.jayce.vexis.business.history.api.OnOptionClickListener
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.DialogTimelineBinding
import com.jayce.vexis.databinding.FragmentHistoryBinding
import com.jayce.vexis.databinding.HistoryMomentEntryBinding
import com.jayce.vexis.domain.bean.MomentEntry
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.domain.route.HistoryService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import org.koin.android.ext.android.inject

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(), OnOptionClickListener {

    private val manager by inject<TimeManager>()
    private val eventHandle = ClickHandle(ClickHandle.Mode.LISTENER)

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
            binding.left.addMoment(it.filter { it.isValid() })
        }
    }

    private fun initView() = binding.apply {
        optionPanel.addOnOptionClickListener(this@HistoryFragment)
        left.setOnMomentClick {
            showMomentDialog(it)
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

        ThreadTool.runOnMulti {
            val pair = manager.getTime()
            axis.updateTimePeriod(pair.first, pair.second)
        }
    }

    private fun showMomentDialog(entry: MomentEntry) {
        val context = activity as? Context ?: return
        FlexibleDialog<HistoryMomentEntryBinding>(context)
            .flexibleView(HistoryMomentEntryBinding::inflate) {
                meassage.text = entry.message
                author.text = "LJW"
                time.text = entry.timeStamp.toTime()
            }
            .cancelable(true)
            .show()
    }

    override fun onScaleChange(factor: Int) {
        val axis = binding.axis
        val param = axis.layoutParams
        param.height = rootWidth * factor
        axis.layoutParams = param
    }

    override fun onTimeChange(start: TimeUnitEntry, end: TimeUnitEntry) {
        binding.axis.updateTimePeriod(start, end)
        ThreadTool.runOnIO {
            manager.setTime(start, end)
            binding.left.updateTime()
            binding.left.invalidate()
        }
    }

    override fun onSearch(type: Int, text: String, time: TimeUnitEntry) { /**/ }
}