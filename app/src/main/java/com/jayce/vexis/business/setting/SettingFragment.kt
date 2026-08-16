package com.jayce.vexis.business.setting

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jayce.vexis.client.AndroidTool.putData
import com.jayce.vexis.client.BaseTool.restartApp
import com.jayce.vexis.client.ThreadTool.runOnMain
import com.jayce.vexis.R
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.FileTool
import com.jayce.vexis.client.FileTool.getDir
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.domain.route.FileService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.util.util.FileUtil.getFileHashAndHead
import java.io.File

class SettingFragment : PreferenceFragmentCompat() {

    private val entryList = arrayListOf<String>()
    private val entryValueList = arrayListOf("key111", "key222")

    private var listPreference: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)
        initPreference()
        initUI()
        loadAvalibleFont()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "changeFont" -> {
                runOnMain {
                    putData("font", "方正粗圆")
                    restartApp()
                }
            }
            "font" -> { /**/ }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun initUI() {
        listPreference?.apply {
            icon = null
            isIconSpaceReserved = false
        }
    }

    private fun initPreference() {
        listPreference = findPreference("font")
        listPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val index = entryValueList.indexOf(newValue.toString())
            preference.summary = entryList[index]
            return@setOnPreferenceChangeListener true
        }
    }

    private fun loadAvalibleFont() {
        val file = getDir(FileTool.Dir.LOC_PRIVATE_FILE, envContext)
        val fontDir = File(file, "font")
        if (!fontDir.exists()) {
            initData()
            return
        }
        val fontFiles = fontDir.listFiles() ?: kotlin.run {
            initData()
            return
        }
        request<FileService, _>({ loadDynamicSubModule("resource", "font") }) { netList ->
            fontFiles.map { fontFile ->
                val netItem = netList[0].entries.find { it.name == fontFile.name } ?: return@map
                val fileHash = getFileHashAndHead(fontFile, "SHA256").first
                if (fileHash != netItem.hash) return@map
                val name = fontFile.name.replace(".ttf", "")
                entryList.add(name)
                entryValueList.add(name)
            }
            ui { initData() }
        }
    }

    private fun initData() {
        listPreference?.apply {
            entries = entryList.toTypedArray()
            entryValues = entryValueList.toTypedArray()
            val defaultValue = if (entryList.isEmpty()) "系统默认" else entryList[0]
            setDefaultValue(defaultValue)
        }
    }
}