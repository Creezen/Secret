package com.jayce.vexis.client.ability.thread

interface ThreadStatus {

    fun fail(throwable: Throwable)

    fun timeOut()

    fun finished()

}