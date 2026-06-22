package com.jayce.vexis.business.setting

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jayce.vexis.client.AndroidTool.putData
import com.jayce.vexis.client.BaseTool.restartApp
import com.jayce.vexis.client.ThreadTool.runOnMain
import com.jayce.vexis.R

class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "changeFont" -> {
                runOnMain {
                    putData("font", "方正粗圆")
                    restartApp()
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}