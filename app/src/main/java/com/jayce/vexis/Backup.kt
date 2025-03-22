package com.jayce.vexis

//val singleThread = Executors.newSingleThreadExecutor { Thread(it, "threadX") }
//    .asCoroutineDispatcher()  + CoroutineName("fff")
//val multiThread = Executors.newFixedThreadPool(3) { Thread(it, "hhh") }.asCoroutineDispatcher() + CoroutineName("ddd")
//CoroutineScope(singleThread).launch {
//    while(true) {
//        Log.d(CreezenService.TAG,"thread1:  ${Thread.currentThread().name}  ${coroutineContext[CoroutineName.Key]?.name }")
//        delay(1000)
//    }
//}
//CoroutineScope(multiThread).launch {
//    while(true) {
//        Log.d(CreezenService.TAG,"thread2:  ${Thread.currentThread().name}  ${coroutineContext[CoroutineName.Key]?.name }")
//        delay(1000)
//    }
//}
//
//private fun threatFunction() {
//    val handleThread = HandlerThread("gfh")
//    handleThread.start()
//    val handle = Handler(handleThread.looper) { msg ->
//        Log.d(CreezenService.TAG, "handleMessage:  ${msg.data}")
//        true
//    }
//    handle.postDelayed(runnable, 2000)
//    val messenger = Messenger(handle)
//    val msg = Message().apply {
//        what = 1
//        replyTo = messenger
//        data = Bundle().apply {
//            putString("s", "fd")
//        }
//    }
//    val valus = PinyinHelper.convertToPinyinArray('长', PinyinFormat.WITH_TONE_MARK)
//    valus.forEach {
//        Log.d(CreezenService.TAG,"pinyin: $it")
//    }
//    messenger.send(msg)
//}
//
//private val runnable = Runnable {
//    Log.d(CreezenService.TAG,"dgfdgff")
//}