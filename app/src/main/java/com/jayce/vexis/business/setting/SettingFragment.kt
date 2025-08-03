package com.jayce.vexis.business.setting

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.R

class SettingFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        preference.key.toast()
        return super.onPreferenceTreeClick(preference)

    }
}