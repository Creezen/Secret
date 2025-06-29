package com.jayce.vexis.senior

import android.os.Bundle
import android.util.Log
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.ThreadTool
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityAdviceBinding
import kotlinx.coroutines.Dispatchers

class AdviceActivity : BaseActivity() {

    companion object {
        const val TAG = "AdviceActivity"
    }

    private lateinit var binding: ActivityAdviceBinding

    private var primaryKey: String = ""
    private var secondKey: String = ""
    private var tertiaryKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initPage()
    }

    private fun initData() {
        primaryKey = intent.getStringExtra("primary") ?: ""
        secondKey = intent.getStringExtra("secord") ?: ""
        tertiaryKey = intent.getStringExtra("tertiary") ?: ""
    }

    private fun initPage() {
        with(binding) {
            content.hint = "留下你对$primaryKey/$secondKey/${tertiaryKey}专业的同学的话吧！"
            submit.setOnClickListener {
                val text = content.msg(true)
                if(text.isBlank()) {
                    "内容不可以为空哦！".toast()
                    return@setOnClickListener
                }
                ThreadTool.runOnMulti(Dispatchers.IO) {
                    Log.d(TAG, "send: $primaryKey/$secondKey/${tertiaryKey}")
                    val flag = NetTool.create<SeniorService>().sendSeniorAdvice(
                        primaryKey,
                        secondKey,
                        tertiaryKey,
                        text
                    ).await()
                    if(flag) {
                        finish()
                    } else {
                        "服务器错误，请重试!!".toast()
                    }
                }
            }
        }
    }

}