package com.jayce.vexis.domain.viewmodel

import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bo.ChatBO
import com.jayce.vexis.domain.database.event.EventEntity
import com.jayce.vexis.foundation.Util.Extension.chat
import com.jayce.vexis.foundation.ability.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

class ChatViewModel(private val repository: EventRepository) : BaseViewModel() {

    val chatFlow: SharedFlow<EventEntity> = repository.chatEventFlow

    private var workJob: Job? = null

    suspend fun getLocalChatList(): Pair<List<ChatBO>, Long>{
        val chatList = repository.getChatEvent()
        if (chatList.first().isEmpty()) return listOf<ChatBO>() to -1
        val lastId = chatList.first().last().id
        val entryList = chatList.first().map { it.chat() }
        return entryList to lastId
    }

    override fun onCleared() {
        super.onCleared()
        workJob?.cancel()
        workJob = null
    }
}