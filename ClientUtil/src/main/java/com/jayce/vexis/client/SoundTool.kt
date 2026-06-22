package com.jayce.vexis.client

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.SoundPool
import android.os.Environment
import androidx.core.content.ContextCompat
import com.jayce.vexis.client.BaseTool.envContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.log10
import kotlin.math.sqrt

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
        soundPool.load(envContext, soundResourceId, 1)
    }

    fun recordSound(context: Context, activity: Activity) {
        val recordDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.let { File(it, "recorder") }
            ?: File(FileTool.getDir(FileTool.Dir.EXT_PUBLIC_ROOT), "recorder")
        if (!recordDir.exists()) {
            recordDir.mkdirs()
        }
        val pcmFile = File(recordDir, "${System.currentTimeMillis()}.pcm")
        if (!pcmFile.exists()) {
            pcmFile.parentFile?.mkdirs()
            pcmFile.createNewFile()
        }
        val fos = FileOutputStream(pcmFile)
        ThreadTool.runOnMulti {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                return@runOnMulti
            }
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
            )
            var count = 0
            record.startRecording()
            while (count < 10000) {
                count++
                val buffer = ByteArray(256)
                val length = record.read(buffer, 0, 256)
                fos.write(buffer, 0, length)
                var sum = 0
                for (i in 0 until length step 2) {
                    val s =
                        ((buffer[i].toInt() and 0xff) or (buffer[i + 1].toInt() shl 8)).toShort()
                    sum += s
                }
                val rms = sqrt(sum / (length / 2f))
                val db = 20 * log10(rms)
                TLog.d("db: $db")
            }
            fos.close()
            record.stop()
            record.release()
            val wavFile = File(pcmFile.parent, pcmFile.name.replace(".pcm", ".wav"))
            pcmToWav(pcmFile, wavFile, 44100, 2, 16)
        }
    }

    private fun pcmToWav(pcmFile: File, wavFile: File, sampleRate: Int, channels: Int, bitDepth: Int) {
        val pcmData = ByteArray(1024)
        FileInputStream(pcmFile).use { fis ->
            FileOutputStream(wavFile).use { fos ->
                val totalAudioLen = fis.channel.size()
                val totalDataLen = totalAudioLen + 36
                val byteRate = sampleRate * channels * bitDepth / 8
                writeWavHeader(fos, totalAudioLen, totalDataLen, sampleRate, channels, byteRate.toLong(), bitDepth)
                var read : Int
                while (fis.read(pcmData).also { read = it } != -1) {
                    fos.write(pcmData, 0, read)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun writeWavHeader(
        out: OutputStream,
        totalAudioLen: Long,
        totalDataLen: Long,
        longSampleRate: Int,
        channels: Int,
        byteRate: Long,
        bitDepth: Int
    ) {
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalDataLen.toInt())
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1)
        writeShort(header, 22, channels.toShort())
        writeInt(header, 24, longSampleRate)
        writeInt(header, 28, byteRate.toInt())
        writeShort(header, 32, (channels * bitDepth / 8).toShort())
        writeShort(header, 34, bitDepth.toShort())
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        writeInt(header, 40, totalAudioLen.toInt())
        out.write(header, 0, 44)
    }

    private fun writeInt(buf: ByteArray, pos: Int, value: Int) {
        buf[pos] = (value and 0xff).toByte()
        buf[pos + 1] = (value shr 8 and 0xff).toByte()
        buf[pos + 2] = (value shr 16 and 0xff).toByte()
        buf[pos + 3] = (value shr 24 and 0xff).toByte()
    }

    private fun writeShort(buf: ByteArray, pos: Int, `val`: Short) {
        buf[pos] = (`val`.toInt() and 0xff).toByte()
        buf[pos + 1] = (`val`.toInt() shr 8 and 0xff).toByte()
    }
}