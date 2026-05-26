package com.jayce.vexis.domain.viewmodel

import com.creezen.commontool.Config.EVENT_TYPE_FEEDBACK
import com.creezen.commontool.Config.EVENT_TYPE_ROLE
import com.jayce.vexis.business.mail.OnEventDeliveryListener
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.foundation.ability.EventRepository
import kotlinx.coroutines.flow.first
import java.util.LinkedList

class MailViewModel(private val repository: EventRepository) : BaseViewModel() {

    val mailFlow = repository.mailEventFlow
    private val totalEventList = LinkedList<EventEntry>()
    private val feedbackEventList = LinkedList<EventEntry>()
    private val managerEventList = LinkedList<EventEntry>()
    private val listenerList = arrayListOf<OnEventDeliveryListener>()

    suspend fun getMailEvent(): Pair<List<EventEntry>, Long> {
        val mailList = repository.getMailEvent().first()
        if (mailList.isEmpty()) return listOf<EventEntry>() to -1
        val lastReadId = mailList.last().id
        return mailList to lastReadId
    }

    fun deliveryEvent(entry: EventEntry) {
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