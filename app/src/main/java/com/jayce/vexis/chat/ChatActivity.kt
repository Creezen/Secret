package com.jayce.vexis.chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendChatMessage
import com.jayce.vexis.CreezenService
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.ability.event.ChatEventService
import com.jayce.vexis.ability.event.ChatEventService.Companion.bindActivity
import com.jayce.vexis.ability.event.ChatEventService.Companion.messageLock
import com.jayce.vexis.onlineUser

class ChatActivity: BaseActivity() {

    companion object {
        private val itemList = arrayListOf<ChatItem>()
        const val TAG = "ChatActivity"
    }

    private lateinit var binding: ActivityChatBinding

    private val adapter by lazy { ChatAdapter(itemList) }
    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatEventService.ServiceBinder
            messageLock.lock()
            binder.onBindForData {
                while(it.size > 0) {
                    updateChatMessage(it.take())
                }
            }
            bindActivity()
            binder.registerReceiveMessage {
                updateChatMessage(it)
            }
            messageLock.unlock()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        val intent = Intent(this, ChatEventService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun updateChatMessage(msg: String) {
        itemList.add(ChatItem(onlineUser.nickname, System.currentTimeMillis().toTime(), msg))
        adapter.notifyItemRangeInserted(itemList.size, 1)
        binding.edit.setText("")
        binding.message.scrollToPosition(adapter.itemCount - 1)
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

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }

}