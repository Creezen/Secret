package com.jayce.vexis.business.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import com.jayce.vexis.business.history.api.OnOptionClickListener
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.DialogTimelineBinding
import com.jayce.vexis.databinding.FragmentHistoryBinding
import com.jayce.vexis.databinding.HistoryMomentEntryBinding
import com.jayce.vexis.domain.bo.MomentBO
import com.jayce.vexis.domain.bo.TimeBO
import com.jayce.vexis.domain.route.HistoryService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.util.toTime
import com.jayce.vexis.util.vo.HistoryVO
import org.koin.android.ext.android.inject

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(), OnOptionClickListener {

    private val manager by inject<TimeManager>()

    private var rootWidth: Int = -1
    private var rootHeight: Int = -1

    private val eventList: ArrayList<HistoryVO> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        updateSize()
        return binding.root
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        queryList()
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
        request<HistoryService, List<HistoryVO>>({ queryAllEvent() }) {
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
            FlexibleDialog.flexibleView<DialogTimelineBinding>(ctx)
                .positive {
                    val time = picker.formatTime()
                    val msg = content.msg()
                    request<HistoryService, _>({ sendEventData(time, msg) }) { it.toast() }
                }
                .show()
        }

        scroll.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (dragger.isDragged) return@setOnScrollChangeListener
            val maxOffset = base.height - scroll.height
            dragger.percent = if (scrollY <= 0) 0.0f
            else if (scrollY >= maxOffset) 1.0f
            else scrollY.toFloat() / maxOffset
        }
        dragger.setOnDragStateChange {
            scroll.parent.requestDisallowInterceptTouchEvent(it)
            floatingBtn.visibility = if (it) View.GONE else View.VISIBLE
        }
        dragger.setOnDrag { scroll.scrollY += (it * (base.height - scroll.height)).toInt() }
        ThreadTool.runOnMain {
            val pair = manager.getTime()
            axis.updateTimePeriod(pair.first, pair.second)
        }
        updateDrag()
    }

    private fun showMomentDialog(entry: MomentBO) {
        val context = activity as? Context ?: return
        FlexibleDialog
            .flexibleView<HistoryMomentEntryBinding>(context) {
                meassage.text = entry.message
                author.text = "LJW"
                time.text = entry.timeStamp.toTime()
            }
            .cancelable(true)
            .show()
    }

    private fun updateDrag() = binding.apply {
        scroll.doOnLayout {
            val viewportHeight = scroll.height - scroll.paddingTop - scroll.paddingBottom
            val isOver = viewportHeight < base.height
            dragger.visibility = if (isOver) View.VISIBLE else View.GONE
        }
    }

    override fun onScaleChange(factor: Int) {
        val axis = binding.axis
        val param = axis.layoutParams
        param.height = rootWidth * factor
        axis.layoutParams = param
        updateDrag()
    }

    override fun onTimeChange(start: TimeBO, end: TimeBO) {
        binding.axis.updateTimePeriod(start, end)
        ThreadTool.runOnIO {
            manager.setTime(start, end)
            binding.left.updateTime()
            binding.left.invalidate()
        }
    }

    override fun onSearch(type: Int, text: String, time: TimeBO) { /**/ }
}