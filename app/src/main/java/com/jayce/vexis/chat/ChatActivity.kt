package com.jayce.vexis.chat

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.DataTool.toData
import com.creezen.tool.DataTool.toJson
import com.creezen.tool.NetTool.sendChatMessage
import com.creezen.tool.ThreadTool
import com.creezen.tool.bean.BlockOption
import com.creezen.tool.contract.LifecycleJob
import com.creezen.tool.enum.ThreadType
import com.google.gson.reflect.TypeToken
import com.jayce.vexis.CreezenService
import com.jayce.vexis.ability.event.EventHandler
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityChatBinding
import com.jayce.vexis.onlineUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ChatActivity: BaseActivity() {

    companion object {
        private val itemList = arrayListOf<ChatItem>()
        const val TAG = "ChatActivity"

        fun getChatList(): ArrayList<ChatItem> {
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
        val cachedData = readPrefs {
            it.getString("chatData", JSONArray().toJson())
        }
        Log.d(TAG,"cached data: $cachedData")
        itemList.clear()
        cachedData?.toData<List<ChatItem>>(object : TypeToken<List<ChatItem>>() {}.type)?.let {
            it.forEach {
                Log.d(TAG,"data: $it")
            }
            itemList.addAll(it)
        }
        scope.launch {
            EventHandler.chatFlow.collect {
                Log.d(TAG,"collect it: $it")
                withContext(Dispatchers.Main) {
                    itemList.add(ChatItem(onlineUser.nickname, System.currentTimeMillis().toTime(), it))
                    adapter.notifyItemRangeInserted(itemList.size, 1)
                    binding.edit.setText("")
                    binding.message.scrollToPosition(adapter.itemCount - 1)
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

    override fun onDestroy() {
        scope.cancel()
        writePrefs {
            itemList.toJson()?.let { listStr ->
                it.putString("chatData", listStr)
            }
        }
        super.onDestroy()
    }
}