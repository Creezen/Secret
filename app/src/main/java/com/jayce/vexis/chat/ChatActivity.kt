package com.jayce.vexis.chat

import android.os.Bundle
import android.util.Log
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

class ChatActivity: BaseActivity() {

    companion object {
        const val TAG = "ChatActivity"
        private val itemList = arrayListOf<ChatItem>()

        fun getChatList(): List<ChatItem> {
            return itemList
        }
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
        Log.d(TAG,"open collect")
        CreezenService.getChatMessage {
            scope.launch {
                while(true) {
                    itemList.add(it.take())
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
                if(content.isEmpty()) {
                    return@setOnClickListener
                }
                sendChatMessage(CreezenService.scope, edit.msg(true))
            }
        }
    }
}