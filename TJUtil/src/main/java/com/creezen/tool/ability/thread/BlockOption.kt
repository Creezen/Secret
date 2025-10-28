package com.creezen.tool.ability.thread

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class BlockOption(
    val type: ThreadType,
    val delayMillis: Long = 0,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val name: String? = null,
    val scope: CoroutineScope? = null
)