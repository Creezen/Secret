package com.jayce.vexis.chat

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendMessage
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding

class ChatActivity: BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private val itemList = arrayListOf<ChatItem>()

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
//        withContext(Dispatchers.Main) {
//            itemList.add(
//                ChatItem(onlineUser.nickname,
//                System.currentTimeMillis().toTime(), line)
//            )
//            adapter.notifyItemRangeInserted(itemList.size, 1)
//            binding.edit.setText("")
//            binding.message.scrollToPosition(adapter.itemCount-1)
//        }

    }

    private fun initView() {
        with(binding) {
            message.layoutManager = LinearLayoutManager(this@ChatActivity)
            message.adapter = adapter
            send.setOnClickListener {
                sendMessage(this@ChatActivity, edit.msg(true))
            }
        }
    }

}