package com.jayce.vexis.business.setting

import android.os.Bundle
import com.creezen.tool.AndroidTool
import com.jayce.vexis.R
import com.jayce.vexis.databinding.ActivitySettingBinding
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.core.base.BaseViewModel

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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