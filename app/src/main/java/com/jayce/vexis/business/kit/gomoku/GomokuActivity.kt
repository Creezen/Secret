package com.jayce.vexis.business.kit.gomoku

import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityGomokuBinding
import com.jayce.vexis.databinding.GomokuDialogBinding
import com.jayce.vexis.domain.viewmodel.GomokuViewModel
import com.jayce.vexis.domain.viewmodel.GomokuViewModel.Companion.FIRST_TO_SECOND
import com.jayce.vexis.domain.viewmodel.GomokuViewModel.Companion.FIRST_TO_THIRD
import com.jayce.vexis.domain.viewmodel.GomokuViewModel.Companion.SECOND_BACK_FIRST
import com.jayce.vexis.domain.viewmodel.GomokuViewModel.Companion.THIRD_BACK_FIRST
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GomokuActivity : BaseActivity<ActivityGomokuBinding>() {

    private var dialog: FlexibleDialog<GomokuDialogBinding>? = null
    private var dialogBinding: GomokuDialogBinding? = null
    private var dialogNavController: NavController? = null
    private val viewModel by viewModel<GomokuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        showDialog()
    }

    private fun initPage() {
        binding.playerBoard.setOnStonePlace { x, y ->
            viewModel.placeChess(x, y, viewModel.isServer)
            binding.playerBoard.placeStone(x, y, viewModel.isServer)
        }
    }

    private fun showDialog() {
        dialog = FlexibleDialog<GomokuDialogBinding>(this)
            .flexibleView(GomokuDialogBinding::inflate) {
                dialogBinding = this
                root.post {
                    val fragment = supportFragmentManager.findFragmentById(R.id.dialogNavigation)
                    dialogNavController = (fragment as NavHostFragment).navController
                }
                initDialogView()
            }
            .cancelable(false)
            .show(FlexibleDialog.FlexibleSize(256, -1))
    }

    private fun initDialogView() {
        dialogBinding?.apply {
            confirm.text = "开启监听"
            cancle.setOnClickListener { finish() }
            confirm.setOnClickListener {
                ThreadTool.runOnMulti {
                    if (viewModel.shouldCheckWifiStatus()) {
                        val manager = getSystemService(WifiManager::class.java)
                        viewModel.isWifiEnabled = manager.isWifiEnabled
                    }
                    viewModel.updateButtonStatus()
                }
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.buttonTextFlow.collect {
                val cancelText = it.first
                val confirmText = it.second
                dialogBinding?.apply {
                    if (cancelText.isEmpty()) {
                        cancle.visibility = View.GONE
                    } else {
                        cancle.visibility = View.VISIBLE
                        cancle.text = cancelText
                    }
                    if (confirmText.isEmpty()) {
                        confirm.visibility = View.GONE
                    } else {
                        confirm.visibility = View.VISIBLE
                        confirm.text = confirmText
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.toastFlow.collect { it.toast() }
        }

        lifecycleScope.launch {
            viewModel.pageFlow.collect {
                val navController = dialogNavController ?: return@collect
                when (it) {
                    FIRST_TO_SECOND -> navController.navigate(R.id.first2Second)
                    SECOND_BACK_FIRST -> navController.popBackStack(R.id.firstNav, false)
                    FIRST_TO_THIRD -> {
                        val bundle = Bundle()
                        bundle.putString("ipAddress", viewModel.ipInput)
                        navController.navigate(R.id.first2Third, bundle)
                    }
                    THIRD_BACK_FIRST -> navController.popBackStack(R.id.firstNav, false)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.dialogFlow.collect {
                dialog?.dismiss()
                dialog = null
            }
        }

        lifecycleScope.launch {
            viewModel.chessFlow.collect {
                binding.playerBoard.placeStone(it.first, it.second, it.third)
            }
        }
    }
}