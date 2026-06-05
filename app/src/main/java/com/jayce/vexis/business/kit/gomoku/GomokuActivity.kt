package com.jayce.vexis.business.kit.gomoku

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityGomokuBinding
import com.jayce.vexis.databinding.ConnectLanViewBinding
import com.jayce.vexis.domain.viewmodel.GomokuViewModel
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GomokuActivity : BaseActivity<ActivityGomokuBinding>() {

    private var dialog: FlexibleDialog<ConnectLanViewBinding>? = null
    private var dialogBinding: ConnectLanViewBinding? = null
    private val viewModel by viewModel<GomokuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        showDialog()
    }

    private fun initPage() {
        val board = binding.playerBoard
        board.setOnStonePlace { x, y ->
            viewModel.sendChessLocation(x, y)
        }
    }

    private fun showDialog() {
        dialog = FlexibleDialog
            .flexibleView<ConnectLanViewBinding>(this){
                dialogBinding = this
                dialogNavigation.addConnectionListener(viewModel)
                initViewModel()
            }
            .cancelable(false)
            .show(FlexibleDialog.FlexibleSize(256, -1))
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.dialogFlow.collect {
                when (it) {
                    true -> finish()
                    false -> {
//                        dialog?.dismiss()
                        dialog = null
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.chessFlow.collect {
                binding.playerBoard.placeStone(it.x, it.y, it.type)
                if (it.shouldSendRemote) {
                    val text = "${it.x}:${it.y}:${it.type}"
                    dialogBinding?.dialogNavigation?.sendMessage(it.type, text)
                }
            }
        }
    }
}