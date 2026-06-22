package com.jayce.vexis.client

class NativeLib {

    companion object {
        init {
            System.loadLibrary("nativetool")
        }
    }

    external fun stringFromJNI(): String
}