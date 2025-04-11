package com.jayce.vexis.chat

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendChatMessage
import com.jayce.vexis.CreezenService
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : BaseActivity() {
    companion object {
        const val TAG = "ChatActivity"
        private val itemList = arrayListOf<ChatItem>()
    }

    private lateinit var binding: ActivityChatBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val adapter by lazy { ChatAdapter(itemList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
    }

    private fun initData() {
        CreezenService.getChatMessage {
            val localList = arrayListOf<ChatItem>()
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
                    withContext(Dispatchers.Main) {
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
                if (content.isEmpty()) {
                    return@setOnClickListener
                }
                sendChatMessage(CreezenService.scope, edit.msg(true))
            }
        }
    }

    override fun onDestroy() {
        CreezenService.sendFinish()
        super.onDestroy()
    }
}
