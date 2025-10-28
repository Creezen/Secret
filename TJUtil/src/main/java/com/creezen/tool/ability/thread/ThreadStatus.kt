package com.creezen.tool.ability.thread

interface ThreadStatus {

    fun fail(throwable: Throwable)

    fun timeOut()

    fun finished()

}