package com.jayce.vexis.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.EventRepository
import com.jayce.vexis.domain.bean.EventEntry
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch

class ChatViewModel(private val repository: EventRepository) : BaseViewModel() {

    private val _eventFlow: MutableSharedFlow<EventEntry> = MutableSharedFlow(0, 256, BufferOverflow.SUSPEND)
    val eventFlow: SharedFlow<EventEntry> = _eventFlow.asSharedFlow()

    private val countDownLatch: CountDownLatch = CountDownLatch(1)

    @Volatile
    private var eventId: Long = -1

    fun collect() {
        viewModelScope.launch {
            val ready = CompletableFuture<Unit>()
            launch {
                ready.complete(Unit)
                repository.flow.collect collect1@ {
                    countDownLatch.await()
                    if (it.id <= eventId) return@collect1
                    _eventFlow.emit(it)
                }
            }

            ready.await()

            val list = repository.getAllEvent().first()
            list.forEach {
                _eventFlow.emit(it)
                eventId = it.id
            }
            countDownLatch.countDown()
        }
    }
}