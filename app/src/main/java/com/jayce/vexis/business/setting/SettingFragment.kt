package com.jayce.vexis.business.setting

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.creezen.tool.AndroidTool
import com.creezen.tool.BaseTool.restartApp
import com.jayce.vexis.R

class SettingFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "changeFont" -> {
                AndroidTool.writePrefs {
                    it.putString("font", "方正粗圆")
                }
                restartApp()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}