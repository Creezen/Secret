package com.jayce.vexis.domain.viewmodel

import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.foundation.Util.Extension.chat
import com.jayce.vexis.foundation.ability.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

class ChatViewModel(private val repository: EventRepository) : BaseViewModel() {

    val chatFlow: SharedFlow<EventEntry> = repository.chatEventFlow

    private var workJob: Job? = null

    suspend fun getLocalChatList(): Pair<List<ChatEntry>, Long>{
        val chatList = repository.getChatEvent()
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