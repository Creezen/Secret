package com.jayce.vexis.business.kit.pinyin

import android.os.Bundle
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.createApi
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.ui
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityPinyinBinding
import com.jayce.vexis.foundation.route.ApiService
import org.json.JSONObject

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
//                getPinyinForChinese(chinese)
                getPinYinString(chinese)
            }
        }
    }

    private fun getPinyinForChinese(chineseChar: String) {
        ThreadTool.runOnMulti {
            val res = createApi<ApiService>()
                .getDictionary(chineseChar)
                .await()
            val jsonObj = JSONObject(res)
            val data = jsonObj.optJSONArray("data")?.get(0) as JSONObject
            val realVal = data.optString("pinyin")
            ui {
                binding.result.text = realVal
            }
        }.onFailure {
            it.printStackTrace()
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