package com.jayce.vexis

fun main() {}
//
// private fun threatFunction() {
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
// }
//
// private val runnable = Runnable {
//    Log.d(CreezenService.TAG,"dgfdgff")
// }

//    private fun test() {
//        val a = binding.longText
//        binding.button.setOnClickListener {
//            val textValue = binding.edit.text.toString()
//            val list = textValue.split("\n")
//                .asSequence()
//                .filterNot {
//                    it.isEmpty()
//                }.map {
//                    it.trim()
//                }.take(10)
//            list.forEach{
//                Log.e("test", "the value: $it")
//            }
//            RandomAccessFile("filepath", "r").use {
//                it.seek(it.filePointer)
//                Channels.newInputStream(it.channel).bufferedReader().useLines {
//
//                }
//            }
//            val file = File("ddd")
//
//            file.useLines {
//                it.filterNot {
//                    it.isEmpty()
//                }.map {
//                    it.trim()
//                }
//            }
//            a.longText = textValue
//            binding.tv.text = "${list.count()}"
//        }
//    }

//        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        })
//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
//            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
//            override fun onActivityResumed(activity: Activity) {}
//            override fun onActivityPaused(activity: Activity) {}
//            override fun onActivityStarted(activity: Activity) {
//                count ++
//                if (count > 0) {
//                    Log.d(TAG,"forground")
//                }
//            }
//            override fun onActivityStopped(activity: Activity) {
//                count --
//                if (count <= 0) {
//                    Log.d(TAG,"back ground")
//                }
//            }
//            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
//            override fun onActivityDestroyed(activity: Activity) {}
//        })
