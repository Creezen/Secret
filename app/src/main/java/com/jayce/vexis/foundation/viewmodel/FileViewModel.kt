package com.jayce.vexis.foundation.viewmodel

import android.util.Log
import com.creezen.tool.FileTool.downloadFileByNet
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.DownloadTask
import com.jayce.vexis.foundation.route.FileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Semaphore
import okhttp3.ResponseBody
import java.util.concurrent.LinkedBlockingQueue

class FileViewModel : BaseViewModel() {

    private val _flow: MutableSharedFlow<Int> = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val progressFlow = _flow.asSharedFlow()

    private val _taskStateFlow: MutableSharedFlow<DownloadTask> = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val taskStateFlow = _taskStateFlow.asSharedFlow()

    private val semaphore = Semaphore(1, 1)

    val taskQueue = LinkedBlockingQueue<DownloadTask>()

    fun pushDownloadTask(value: DownloadTask) {
        taskQueue.put(value)
    }

    fun fetchAndHandleTask() {
        ThreadTool.runOnMulti(Dispatchers.IO) {
            while (true) {
                val task = taskQueue.take()
                val totalSize = task.size
                val showTaskInfo = task.copy(taskLastCount = taskQueue.size + 1)
                _taskStateFlow.emit(showTaskInfo)
                request<FileService, ResponseBody>({ downloadFile(task.fileName) }) {
                    val stream = it.byteStream()
                    downloadFileByNet(stream, task.fileName) {
                        _flow.emit(it)
                        if (it >= totalSize) {
                            semaphore.release()
                            _flow.emit(0)
                        }
                        if (taskQueue.size <= 0) {
                            _taskStateFlow.emit(DownloadTask("", "所有任务下载完成", -1, -1, 0))
                        }
                    }
                }
                semaphore.acquire()
            }
        }
    }

    fun handleSizeDisplay(size: Long): String {
        if (size < 1024) {
            return "$size b"
        }
        if (size < 1024 * 1024) {
            val sizeNum = size / 1024.0
            val finalNum = "%.2f".format(sizeNum)
            return "$finalNum kb"
        }
        val sizeNum = size / (1024.0 * 1024)
        val finalNum = "%.2f".format(sizeNum)
        return "$finalNum M"
    }
}