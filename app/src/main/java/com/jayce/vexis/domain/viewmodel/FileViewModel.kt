package com.jayce.vexis.domain.viewmodel

import com.jayce.vexis.client.FileTool.downloadFileByNet
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bean.DownloadTask
import com.jayce.vexis.domain.route.FileService
import com.jayce.vexis.foundation.Util.request
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Semaphore
import okhttp3.ResponseBody
import java.util.concurrent.LinkedBlockingQueue

class FileViewModel : BaseViewModel() {

    private val _progressFlow: MutableSharedFlow<Int> =
        MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val progressFlow = _progressFlow.asSharedFlow()

    private val _taskStateFlow: MutableSharedFlow<DownloadTask> = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val taskStateFlow = _taskStateFlow.asSharedFlow()

    private val _taskCountFlow: MutableSharedFlow<Int> = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val taskCountFlow = _taskCountFlow.asSharedFlow()

    private val semaphore = Semaphore(1, 1)

    private val taskQueue = LinkedBlockingQueue<DownloadTask>()

    fun dispatchDownloadTask(value: DownloadTask) {
        taskQueue.put(value)
        runOnIO { _taskCountFlow.emit(taskQueue.size) }
    }

    fun startListen() {
        ThreadTool.runOnMulti {
            while (true) {
                val task = taskQueue.take()
                val totalSize = task.size
                val showTaskInfo = task.copy(taskLastCount = taskQueue.size)
                _taskStateFlow.emit(showTaskInfo)
                request<FileService, ResponseBody>({ downloadFile(task.fileName) }) {
                    val stream = it.byteStream()
                    downloadFileByNet(stream, task.fileName) {
                        _progressFlow.emit(it)
                        if (it >= totalSize) {
                            task.onFinish?.invoke()
                            semaphore.release()
                        }
                    }
                }
                semaphore.acquire()
                if (taskQueue.size < 1) {
                    _taskStateFlow.emit(DownloadTask("", "所有任务下载完成", -1, -1, 0))
                }
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