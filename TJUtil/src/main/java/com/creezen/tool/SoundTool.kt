package com.creezen.tool

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import com.creezen.tool.BaseTool.env

object SoundTool {

    fun init() {}

    private val attribute by lazy {
        AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .build()
    }

    private val soundPool by lazy {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attribute)
            .build()
    }

    init {
        soundPool.setOnLoadCompleteListener { _, i, i2 ->
            soundPool.play(i, 1F, 1F, 1, 0, 1F)
        }
    }

    fun playShortSound(soundResourceId: Int) {
        soundPool.load(env(), soundResourceId, 1)
    }
}