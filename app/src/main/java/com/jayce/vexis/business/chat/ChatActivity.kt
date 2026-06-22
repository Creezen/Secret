package com.jayce.vexis.business.chat

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.NetTool.sendChatMessage
import com.jayce.vexis.client.ThreadTool.getScope
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.viewmodel.ChatViewModel
import com.jayce.vexis.foundation.Util.Extension.chat
import com.jayce.vexis.foundation.ability.EventRepository.Companion.SCOPE_EVENT
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatActivity : BaseActivity<ActivityChatBinding>() {

    private val itemList = arrayListOf<ChatEntry>()
    private val adapter by lazy { ChatAdapter(itemList) }
    private val viewModel by viewModel<ChatViewModel>()

    private val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager ?: return
            for (i in 0 until layoutManager.childCount) {
                val child = layoutManager.getChildAt(i) ?: continue
                val holder = recyclerView.getChildViewHolder(child) as? ChatAdapter.ViewHolder ?: return
                adapter.markItemReadIfNeed(holder)
            }
        }
    }

    private var lastReadId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.chatFlow.onSubscription {
                val chatPair = viewModel.getLocalChatList()
                val list = chatPair.first
                adapter.notifyDataChange(list)
                binding.message.scrollToPosition(list.size - 1)
                if (lastReadId < chatPair.second) lastReadId = chatPair.second
            }.collect { ui {
                if (it.id <= lastReadId) return@ui
                adapter.getAttachedList().add(it.chat())
                val size = adapter.getAttachedList().size
                adapter.notifyItemRangeInserted(size, 1)
                binding.edit.setText(NIL)
                binding.message.scrollToPosition(size - 1)
            } }
        }
    }

    private fun initView() = binding.apply {
        message.layoutManager = LinearLayoutManager(this@ChatActivity)
        message.adapter = adapter
        send.setOnClickListener {
            val content = edit.msg(true)
            if (content.isEmpty()) return@setOnClickListener
            val scope = getScope(SCOPE_EVENT) ?: return@setOnClickListener
            sendChatMessage(scope, content)
        }
        message.addOnScrollListener(listener)
    }
}