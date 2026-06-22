package com.jayce.vexis.client

import android.util.Log
import java.util.concurrent.ThreadPoolExecutor

object TLog {

    fun d(msg: String, debugThread: Boolean = true) {
        Log.d("LJW", getMessage(msg, debugThread))
    }

    fun w(msg: String, debugThread: Boolean = true) {
        Log.w("LJW", getMessage(msg, debugThread))
    }

    fun i(msg: String, debugThread: Boolean = true) {
        Log.i("LJW", getMessage(msg, debugThread))
    }

    fun e(msg: String, debugThread: Boolean = true) {
        Log.e("LJW", getMessage(msg, debugThread))
    }

    private fun getMessage(msg: String, debugThread: Boolean): String {
        val threadName = Thread.currentThread().name
        return if (debugThread) {
            "[$threadName] $msg [${getThreadInfo()}]"
        } else {
            "[$threadName] $msg"
        }
    }

    /**
     * executor.activeCount,          // 活动线程数
     * executor.poolSize,             // 当前线程数
     * executor.largestPoolSize,      // 历史最大并发数
     * executor.queue.size,           // 等待队列任务数
     * executor.completedTaskCount,   // 已完成任务数
     * executor.taskCount             // 已提交任务总数（包括已完成）
     */
    private fun getThreadInfo(): String {
        val executor = ThreadTool.multi.executor as? ThreadPoolExecutor ?: return ""
        executor.apply {
            return """
            运行中：$activeCount 等待中：${queue.size} 已完成：$completedTaskCount 总提交：$taskCount 当前总数：$poolSize 历史最多：$largestPoolSize
        """.trimIndent()
        }
    }
}