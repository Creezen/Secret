package com.jayce.vexis.chat

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.DataTool.toTime
import com.jayce.vexis.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.onlineSocket
import com.jayce.vexis.onlineUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicBoolean

class ChatActivity: BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private var socket = onlineSocket
    private lateinit var bufWriter : BufferedWriter
    private lateinit var bufReader : BufferedReader
    private val itemList = arrayListOf<ChatItem>()
    private val socketFlag = AtomicBoolean()
    private val adapter by lazy {
        ChatAdapter(itemList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSocket()
        initView()
    }

    private fun initSocket() {
        socket.apply {
            bufWriter = BufferedWriter(OutputStreamWriter(getOutputStream(), "UTF-8"))
            bufReader = BufferedReader(InputStreamReader(getInputStream(), "UTF-8"))
            socketFlag.set(true)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                val line = bufReader.readLine()
                if (socket.isClosed || line.isNullOrEmpty()) {
                    socketFlag.set(false)
                    Log.e("ChatActivity.initSocket","服务器错误")
                    break
                }
                withContext(Dispatchers.Main) {
                    itemList.add(
                        ChatItem(onlineUser.nickname,
                        System.currentTimeMillis().toTime(), line)
                    )
                    adapter.notifyItemRangeInserted(itemList.size, 1)
                    binding.edit.setText("")
                    binding.message.scrollToPosition(adapter.itemCount-1)
                }
            }
        }
    }

    private fun initView() {
        with(binding) {
            message.layoutManager = LinearLayoutManager(this@ChatActivity)
            message.adapter = adapter
            send.setOnClickListener {
                if (socket.isClosed || socket.isOutputShutdown || !socketFlag.get()) {
                    "与服务器连接失败，请检查网络".toast()
                    return@setOnClickListener
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    kotlin.runCatching {
                        if (edit.msg(true).isEmpty()) {
                            return@launch
                        }
                        bufWriter.write(edit.msg(true)+"\n")
                        bufWriter.flush()
                    }.onFailure {
                        Log.e("ChatActivity.initView","$it")
                    }
                }
            }
        }
    }

}