package com.jayce.vexis.business.kit

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.business.kit.gomoku.GomokuActivity
import com.jayce.vexis.business.kit.ledger.LedgerSheetActivity
import com.jayce.vexis.business.kit.maze.MazeActivity
import com.jayce.vexis.business.kit.pinyin.PinyinActivity
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.WidgetsBinding

class KitFragment : BaseFragment<WidgetsBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        with(binding) {
            ledger.setOnClickListener {
                startActivity(Intent(context, LedgerSheetActivity::class.java))
            }
            maze.setOnClickListener {
                startActivity(Intent(context, MazeActivity::class.java))
            }
            gomoku.setOnClickListener {
                startActivity(Intent(context, GomokuActivity::class.java))
            }
            pinyin.setOnClickListener {
                startActivity(Intent(context, PinyinActivity::class.java))
            }
            quickStart.setOnClickListener {
                val component = ComponentName("com.DefaultCompany.Myproject", "com.unity3d.player.UnityPlayerActivity")
                val intent = Intent()
                intent.component = component
                startActivity(intent)
            }
        }
        return binding.root
    }
}