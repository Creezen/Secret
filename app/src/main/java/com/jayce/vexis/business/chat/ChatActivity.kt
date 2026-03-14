package com.jayce.vexis.business.chat

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendChatMessage
import com.creezen.tool.ThreadTool.getScope
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.viewmodel.ChatViewModel
import com.jayce.vexis.foundation.Util.Extension.chat
import com.jayce.vexis.foundation.ability.EventHandle.NAME_MESSAGE_SCOPE
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.CompletableFuture

class ChatActivity : BaseActivity<ActivityChatBinding>() {

    companion object {
        const val TAG = "ChatActivity"
        private val itemList = arrayListOf<ChatEntry>()
    }

    private val adapter by lazy { ChatAdapter(itemList) }
    private val viewModel by viewModel<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            val ready = CompletableFuture<Unit>()
            launch {
                val oldSize = itemList.size
                itemList.clear()
                adapter.notifyItemRangeRemoved(0, oldSize)
                ready.complete(Unit)
                viewModel.eventFlow.collect {
                    itemList.add(it.chat())
                    ui {
                        adapter.notifyItemRangeInserted(itemList.size, 1)
                        binding.edit.setText(EMPTY_STRING)
                        binding.message.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
            ready.await()
            viewModel.collect()
        }
    }

    private fun initView() {
        with(binding) {
            message.layoutManager = LinearLayoutManager(this@ChatActivity)
            message.adapter = adapter
            send.setOnClickListener {
                val content = edit.msg(true)
                if (content.isNotEmpty()) {
                    getScope(NAME_MESSAGE_SCOPE)?.let {
                        sendChatMessage(it, content)
                    }
                }
            }
        }
    }
}