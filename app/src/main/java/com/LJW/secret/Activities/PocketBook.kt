package com.ljw.secret.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ljw.secret.R
import com.ljw.secret.activities.BaseActivity
import com.ljw.secret.databinding.PocketBookBinding
import com.ljw.secret.fragments.pocket.MainMenu
import com.ljw.secret.replaceFragment

class PocketBook: BaseActivity() {

    private lateinit var binding: PocketBookBinding
    private lateinit var mainMenu: MainMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=PocketBookBinding.inflate(layoutInflater)
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