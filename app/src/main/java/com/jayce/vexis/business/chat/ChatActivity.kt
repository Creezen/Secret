package com.jayce.vexis.business.chat

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.Config.NIL
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendChatMessage
import com.creezen.tool.ThreadTool.getScope
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.viewmodel.ChatViewModel
import com.jayce.vexis.foundation.Util.Extension.chat
import com.jayce.vexis.foundation.ability.EventRepository.Companion.MESSAGE_SCOPE
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatActivity : BaseActivity<ActivityChatBinding>() {

    private val itemList = arrayListOf<ChatEntry>()
    private val adapter by lazy { ChatAdapter(itemList) }
    private val viewModel by viewModel<ChatViewModel>()

    private var lastReadId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LJW", "onCreate")
        initView()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.chatFlow.onSubscription {
                Log.d("LJW", "onSubscription")
                val chatPair = viewModel.getLocalChatList()
                val list = chatPair.first
                adapter.notifyDataChange(list)
                binding.message.scrollToPosition(list.size - 1)
                lastReadId = chatPair.second
            }.collect {
                Log.d("LJW", "collect")
                ui {
                    if (it.id <= lastReadId) return@ui
                    adapter.getAttachedList().add(it.chat())
                    val size = adapter.getAttachedList().size
                    adapter.notifyItemRangeInserted(size, 1)
                    binding.edit.setText(NIL)
                    binding.message.scrollToPosition(size - 1)
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
                    getScope(MESSAGE_SCOPE)?.let {
                        sendChatMessage(it, content)
                    }
                }
            }
        }
    }
}