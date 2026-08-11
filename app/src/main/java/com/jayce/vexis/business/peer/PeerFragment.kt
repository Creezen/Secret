package com.jayce.vexis.business.peer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.client.AndroidTool.adjustText
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.MajorSelectorBinding
import com.jayce.vexis.databinding.SageFragmentBinding
import com.jayce.vexis.domain.route.PeerService
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.util.dto.PeerDTO
import com.jayce.vexis.util.vo.PeerVO

class PeerFragment : BaseFragment<SageFragmentBinding>() {

    private var discipline = "哲学"
    private var major = "哲学类"
    private var track = "哲学"
    private var originSize: Float = 0f

    private val list = arrayListOf<PeerVO>()
    private val adapter by lazy { PeerAdapter(requireActivity(), list) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        fetchAdvice()
    }

    private fun initView() = binding.apply {
        originSize = majorView.textSize
        subjectView.setOnClickListener {
            val ctx = activity ?: return@setOnClickListener
            FlexibleDialog
                .flexibleView<MajorSelectorBinding>(ctx) {
                    majorPicker.init(discipline, major, track)
                }
                .title("请选择专业")
                .positive {
                    val data = majorPicker.value()
                    discipline = data[0]
                    major = data[1]
                    track = data[2]
                    disciplineView.text = discipline
                    majorView.text = major
                    trackView.text = track
                    disciplineView.adjustText(originSize)
                    majorView.adjustText(originSize)
                    trackView.adjustText(originSize)
                    fetchAdvice()
                }
                .show()
        }
        advice.setOnClickListener {
            jumpTo(PeerActivity::class.java) {
                putExtra("discipline", discipline)
                putExtra("major", major)
                putExtra("track", track)
            }
        }
        adviceRv.layoutManager = LinearLayoutManager(this@PeerFragment.context)
        adviceRv.adapter = adapter
    }

    private fun fetchAdvice() {
        if (discipline.isEmpty() || major.isEmpty() || track.isEmpty()) return
        request<PeerService, _>({ getAdvice(discipline, major, track) }) {
            adapter.notifyDataChange(it)
        }
    }
}