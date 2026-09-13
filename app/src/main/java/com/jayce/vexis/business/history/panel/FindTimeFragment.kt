package com.jayce.vexis.business.history.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jayce.vexis.business.history.api.OnViewReady
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.HistoryPanelFindTimeBinding
import com.jayce.vexis.domain.viewmodel.HistoryViewModel

class FindTimeFragment : BaseFragment<HistoryPanelFindTimeBinding>() {

    private val viewModel by viewModels<HistoryViewModel>(
        ownerProducer = { requireParentFragment() }
    )
    private var onViewReady: OnViewReady? = null

    fun setOnViewReadyListener(listener: OnViewReady) {
        this.onViewReady = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady?.onReady(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }
}