package com.jayce.vexis.business.kit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.foundation.base.BaseFragment
import com.jayce.vexis.databinding.WidgetsBinding
import com.jayce.vexis.business.kit.gomoku.GomokuActivity
import com.jayce.vexis.business.kit.maze.MazeActivity
import com.jayce.vexis.business.kit.ledger.LedgerSheet

class Kit : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = WidgetsBinding.inflate(inflater)
        with(binding) {
            ledger.setOnClickListener {
                startActivity(Intent(context, LedgerSheet::class.java))
            }
            maze.setOnClickListener {
                startActivity(Intent(context, MazeActivity::class.java))
            }
            gomoku.setOnClickListener {
                startActivity(Intent(context, GomokuActivity::class.java))
            }
        }
        return binding.root
    }
}
