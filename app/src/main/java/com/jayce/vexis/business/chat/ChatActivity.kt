package com.jayce.vexis.business.chat

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendChatMessage
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.foundation.ability.EventHandle.getChatMessage
import com.jayce.vexis.foundation.ability.EventHandle.sendFinish
import com.jayce.vexis.foundation.bean.ChatEntry
import com.jayce.vexis.foundation.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ChatActivity : BaseActivity<ActivityChatBinding>() {

    companion object {
        const val TAG = "ChatActivity"
        private val itemList = arrayListOf<ChatEntry>()
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val adapter by lazy { ChatAdapter(itemList) }
    private val viewModel by inject<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        getChatMessage {
            val localList = arrayListOf<ChatEntry>()
            while (it.isNotEmpty()) {
                localList.add(it.take())
            }
            itemList.addAll(localList)
            val dataSize = localList.size
            val afterSize = itemList.size
            adapter.notifyItemRangeInserted(afterSize - dataSize, dataSize)
            binding.message.scrollToPosition(afterSize - 1)
            scope.launch {
                while (true) {
                    val msg = it.take()
                    if (msg.msg.isEmpty()) break
                    itemList.add(msg)
                    ui {
                        adapter.notifyItemRangeInserted(itemList.size, 1)
                        binding.edit.setText("")
                        binding.message.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        }
    }

    private fun initView() {
        with(binding) {
            message.layoutManager = LinearLayoutManager(this@ChatActivity)
            message.adapter = adapter
            send.setOnClickListener {
                val content = edit.msg(true)
                if (content.isNotEmpty()) {
                    sendChatMessage(CoreService.scope, content)
                }
            }
        }
    }

    override fun onDestroy() {
        sendFinish()
        super.onDestroy()
    }
}