package com.jayce.vexis.utility

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.WidgetsBinding
import com.jayce.vexis.utility.ledger.Ledger

class Utility: BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = WidgetsBinding.inflate(inflater)
        with(binding){
            pocketBook.setOnClickListener {
                startActivity(Intent(context, Ledger::class.java))
            }
        }
        return binding.root
    }
}
