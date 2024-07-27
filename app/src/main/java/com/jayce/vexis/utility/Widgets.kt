package com.jayce.vexis.utility

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.BaseFragment
import com.jayce.vexis.utility.ledger.PocketBook
import com.jayce.vexis.databinding.WidgetsBinding

class Widgets: BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = WidgetsBinding.inflate(inflater)
        with(binding){
            pocketBook.setOnClickListener {
                startActivity(Intent(context, PocketBook::class.java))
            }
        }
        return binding.root
    }
}
