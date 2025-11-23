package com.jayce.vexis.nativetool

class NativeLib {

    /**
     * A native method that is implemented by the 'nativetool' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'nativetool' library on application startup.
        init {
            System.loadLibrary("nativetool")
        }
    }
}