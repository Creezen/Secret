package com.jayce.vexis.business.setting

import android.os.Bundle
import com.creezen.tool.AndroidTool
import com.jayce.vexis.R
import com.jayce.vexis.databinding.ActivitySettingBinding
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.foundation.base.BaseViewModel

class SettingActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {
        AndroidTool.replaceFragment(
            supportFragmentManager,
            R.id.settingLayout,
            SettingFragment(),
            "MainSetting"
        )
    }

}