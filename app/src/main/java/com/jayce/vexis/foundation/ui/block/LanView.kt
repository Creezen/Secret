package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.R
import com.jayce.vexis.databinding.LanViewBinding
import com.jayce.vexis.foundation.ability.socket.InnerLanStatusListener
import com.jayce.vexis.foundation.ability.socket.LanConnectionListener
import com.jayce.vexis.foundation.ability.socket.LanManager
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.FIRST_TO_SECOND
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.FIRST_TO_THIRD
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.SECOND_BACK_FIRST
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.THIRD_BACK_FIRST
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LanView(context: Context, attr: AttributeSet) :
    LinearLayout(context, attr), InnerLanStatusListener, KoinComponent {

    private val binding: LanViewBinding = LanViewBinding.inflate(LayoutInflater.from(context), this)
    private val cancel: Button
    private val confirm: Button
    private val navController: NavController

    private val manager by inject<LanManager>()
    private var connectionListener: LanConnectionListener? = null

    init {
        orientation = VERTICAL
        val buttonGroup = binding.button
        cancel = buttonGroup.cancel
        confirm = buttonGroup.confirm
        val fragment = binding.lanNavigation.getFragment<NavHostFragment>()
        navController = fragment.navController
        initButton()
    }

    fun addConnectionListener(listener: LanConnectionListener) {
        connectionListener = listener
        manager.addInnerListener(this, listener)
    }

    private fun initButton() {
        binding.button.cancel.setOnClickListener {
            connectionListener?.onCancel()
        }
        binding.button.confirm.setOnClickListener {
            if (manager.shouldCheckWifiStatus()) {
                val wifiManager = context.getSystemService(WifiManager::class.java)
                manager.isWifiEnabled = wifiManager.isWifiEnabled
            }
            manager.updateButtonStatus()
        }
    }

    override fun onToast(text: String) = text.toast()

    override fun onActionChange(cancelLabel: String, confirmLabel: String) {
        if (cancelLabel.isEmpty()) {
            cancel.visibility = View.GONE
        } else {
            cancel.visibility = View.VISIBLE
            cancel.text = cancelLabel
        }
        if (confirmLabel.isEmpty()) {
            confirm.visibility = View.GONE
        } else {
            confirm.visibility = View.VISIBLE
            confirm.text = confirmLabel
        }
    }

    override fun onStageChange(stage: Int) {
        connectionListener?.onStageChange(stage)
        when (stage) {
            FIRST_TO_SECOND -> navController.navigate(R.id.first2Second)
            SECOND_BACK_FIRST -> navController.popBackStack(R.id.firstNav, false)
            FIRST_TO_THIRD -> {
                val bundle = Bundle()
                bundle.putString("ipAddress", manager.ipInput)
                navController.navigate(R.id.first2Third, bundle)
            }
            THIRD_BACK_FIRST -> navController.popBackStack(R.id.firstNav, false)
        }
    }

    fun sendMessage(type: Int, message: String) {
        manager.sendMessage(type, message)
    }
}