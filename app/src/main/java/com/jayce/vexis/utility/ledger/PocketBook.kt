package com.jayce.vexis.utility.ledger

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jayce.vexis.BaseActivity
import com.jayce.vexis.R
import com.jayce.vexis.databinding.PocketBookBinding
import com.creezen.tool.AndroidTool.replaceFragment

class PocketBook: BaseActivity() {

    private lateinit var binding: PocketBookBinding
    private lateinit var mainMenu: MainMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PocketBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        replaceFragment(mainMenu)
    }

    private fun initView(){
        mainMenu = MainMenu()
    }

    private fun replaceFragment(fragment: Fragment) {
        replaceFragment(supportFragmentManager, R.id.pocketFrame, fragment)
    }
}