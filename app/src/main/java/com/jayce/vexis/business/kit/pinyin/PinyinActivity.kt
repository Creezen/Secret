package com.jayce.vexis.business.kit.pinyin

import android.os.Bundle
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityPinyinBinding

class PinyinActivity : BaseActivity<ActivityPinyinBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            convert.setOnClickListener {
                val chinese = content.msg()
                if (chinese.isEmpty()) return@setOnClickListener
                getPinYinString(chinese)
            }
        }
    }

    private fun getPinYinString(chinese: String) {
        val result = PinyinHelper.convertToPinyinString(chinese, " ", PinyinFormat.WITH_TONE_MARK)
        val multi = PinyinHelper.convertToPinyinArray(chinese[0])
        multi.forEach {
            it.toast()
        }
    }
}