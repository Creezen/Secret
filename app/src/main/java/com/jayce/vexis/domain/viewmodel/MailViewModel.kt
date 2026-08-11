package com.jayce.vexis.domain.viewmodel

import com.jayce.vexis.util.Config.EVENT_TYPE_FEEDBACK
import com.jayce.vexis.util.Config.EVENT_TYPE_ROLE
import com.jayce.vexis.business.mail.OnEventDeliveryListener
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.database.event.EventEntity
import com.jayce.vexis.foundation.ability.EventRepository
import kotlinx.coroutines.flow.first
import java.util.LinkedList

class MailViewModel(private val repository: EventRepository) : BaseViewModel() {

    val mailFlow = repository.mailEventFlow
    private val totalEventList = LinkedList<EventEntity>()
    private val feedbackEventList = LinkedList<EventEntity>()
    private val managerEventList = LinkedList<EventEntity>()
    private val listenerList = arrayListOf<OnEventDeliveryListener>()

    suspend fun getMailEvent(): Pair<List<EventEntity>, Long> {
        val mailList = repository.getMailEvent().first()
        if (mailList.isEmpty()) return listOf<EventEntity>() to -1
        val lastReadId = mailList.last().id
        return mailList to lastReadId
    }

    fun deliveryEvent(entry: EventEntity) {
        totalEventList.add(0, entry)
        when (entry.type) {
            EVENT_TYPE_FEEDBACK -> feedbackEventList.add(0, entry)
            EVENT_TYPE_ROLE -> managerEventList.add(0, entry)
        }
        notifyOnDelivery()
    }

    fun getTotalEventList() = ArrayList(totalEventList)

    fun getFeedbackEventList() = ArrayList(feedbackEventList)

    fun getManagerEventList() = ArrayList(managerEventList)

    fun registerOnDeliveryListener(listener: OnEventDeliveryListener) = listenerList.add(listener)

    private fun removeAllDeliveryListener() = listenerList.clear()

    private fun notifyOnDelivery() {
        listenerList.forEach { it.onDelivery() }
    }

    override fun onCleared() {
        super.onCleared()
        removeAllDeliveryListener()
    }
}