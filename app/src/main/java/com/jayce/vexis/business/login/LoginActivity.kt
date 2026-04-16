package com.jayce.vexis.business.login

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.creezen.commontool.bean.ApkSimpleInfo
import com.creezen.commontool.bean.TransferStatusBean
import com.creezen.commontool.bean.UserBean
import com.creezen.commontool.toBean
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.BaseTool
import com.creezen.tool.FileTool
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.destroySocket
import com.creezen.tool.SoundTool.playShortSound
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.ui
import com.creezen.tool.ability.thread.BlockOption
import com.creezen.tool.ability.thread.ThreadType
import com.jayce.vexis.R
import com.jayce.vexis.business.main.MainActivity
import com.jayce.vexis.business.role.register.RegisterActivity
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.liveUser
import com.jayce.vexis.core.SessionManager.registerUser
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityLoginBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.dynamic.ModuleHelper
import com.jayce.vexis.domain.route.PackageService
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.ability.Logger
import com.jayce.vexis.foundation.ui.animator.MyCustomTransformer
import kotlinx.coroutines.Dispatchers
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.log10
import kotlin.math.sqrt

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private val list = arrayListOf<String>()
    private val picAdapter by lazy {
        HomePagePicAdapter(this, list)
    }
    val liveData = MutableLiveData<String>()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            val binder = service as CoreService.ConnectionBinder
            val notification = buildNotification()
            binder.showNotification(notification)
        }

        override fun onServiceDisconnected(name: ComponentName?) { /**/ }
    }

    private fun buildNotification(): Notification {
        getSystemService(NotificationManager::class.java).apply {
            val notifyChannel =
                NotificationChannel("1", "login", NotificationManager.IMPORTANCE_HIGH)
            createNotificationChannel(notifyChannel)
        }
        val notification = NotificationCompat.Builder(BaseTool.env(), "1")
            .setSmallIcon(R.drawable.tianji)
            .setContentTitle(getString(R.string.login_success_notify))
            .setContentText(getString(R.string.welcome_user, liveUser.nickname))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
        return notification
    }

    @Logger(a = "hello")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.fitsSystemWindows = true
        setContentView(binding.root)
        getNewestVersion()
        initView()
        setAnimation()
        ModuleHelper.test(this, "debug-1.apk")
    }

    override fun onDestroy() {
        super.onDestroy()
        destroySocket()
        unbindService(connection)
    }

    private fun getNewestVersion() {
        request<PackageService, ApkSimpleInfo>({ getVersion() }) {
            ui { "${it.modifyTime.toTime()}  $it.versionName".toast() }
        }
    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.login_button_anim)
        binding.login.startAnimation(animation)
    }

    private fun initView() {
        with(binding) {
            lifecycleOwner = this@LoginActivity
            env = this@LoginActivity
            liveData.observe(this@LoginActivity) {
                login.isClickable = it.length in 6..18
            }
            findPassword.setOnClickListener {
//                recordSound()
//                installApp()
                getString(R.string.not_support).toast()
            }
            createAccount.setOnClickListener {
                playShortSound(R.raw.delete)
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.putExtra("intentAccount", name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                login.isClickable = false
                playShortSound(R.raw.click)

                val option = BlockOption(ThreadType.SINGLE, 2000L, Dispatchers.Main)
                ThreadTool.runWithBlocking(option) {
                    request<UserService, TransferStatusBean>({ loginSystem(name.msg(), password.msg()) }) {
                        Log.e(TAG, "return message: $it")
                        when (it.statusCode) {
                            -1 -> {
                                it.data.toBean<UserBean>()?.let { user ->
                                    registerUser(user)
                                    NetTool.setUser(user)
                                }
                                bindService(
                                    Intent(this@LoginActivity, CoreService::class.java),
                                    connection,
                                    BIND_AUTO_CREATE
                                )
                            }
                            0 -> getString(R.string.no_account_and_create).toast()
                            -3 -> getString(R.string.account_login_other_device).toast()
                            else -> getString(R.string.password_error).toast()
                        }
                    }
                }.onTimedOut {
                    login.isClickable = true
                }.onComplete {
                    login.isClickable = true
                }
            }
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
            val verMsg = "APP : v${packageInfo.versionName}  ${packageInfo.lastUpdateTime.toTime()}"
            versionMsg.text = verMsg
            liveData.value = getString(R.string.login_name)
            password.setText(R.string.login_password)

            initPicture()
        }
    }

    private fun aiModel() {
        kotlin.runCatching {
            val model = Module.load(assetFilePath(this, "model/androidModel.pt"))
            val tensor = Tensor.fromBlob(floatArrayOf(2.5f, 1.5f), longArrayOf(1, 2))
            val result = model.forward(IValue.from(tensor)).toTensor()
            result.dataAsFloatArray[0].toast()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        if (file.exists().not()) {
            file.parentFile?.mkdir()
            file.createNewFile()
        }

        context.assets.open(assetName).use { ins ->
            FileOutputStream(file).use { ous ->
                val buffer = ByteArray(4 * 1024)
                while (true) {
                    val read = ins.read(buffer)
                    if (read != -1) {
                        ous.write(buffer, 0, read);
                    } else break
                }
                ous.flush();
            }
        }
        return  file.absolutePath
    }

    private fun saveToGallery(view: View) {
        view.post {
            val map = view.drawToBitmap(Bitmap.Config.ARGB_8888)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}a.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            uri?.let {
                kotlin.runCatching {
                    contentResolver.openOutputStream(uri)?.use { stream ->
                        map.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    }
                }.onFailure {
                    Log.e("LJW", "save error")
                }
            }
        }
    }

    private fun installApp() {
        val path = FileTool.getDir(FileTool.Dir.LOC_PRIVATE_FILE, this)
        val file = File("$path/game.apk")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val fileUri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
        startActivity(intent)
    }

    private fun recordSound() {
        val recordDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.let { File(it, "recorder") }
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
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
                    val s = ((buffer[i].toInt() and 0xff) or (buffer[i +1].toInt() shl 8)).toShort()
                    sum += s
                }
                val rms = sqrt(sum / (length / 2f))
                val db = 20 * log10(rms)
                Log.d("LJW", "db: $db")
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

    private fun initPicture() {
        binding.iv1.apply {
            this.adapter = picAdapter
            offscreenPageLimit = 2
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            val childAt = getChildAt(0)
            childAt.setPadding(0, 0, 0, 0)
            clipToPadding = false
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            list.add("${BASE_FILE_PATH}hXklnq1757767353397.jpg")
            list.add("${BASE_FILE_PATH}GsMmcS1748872115532.jpg")
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            picAdapter.notifyItemRangeInserted(0, list.size)
            setCurrentItem(1, false)
            setPageTransformer(MyCustomTransformer())
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state != ViewPager2.SCROLL_STATE_IDLE) return
                    if (currentItem == list.size - 1) {
                        post {
                            setCurrentItem(1, false)
                        }
                    }
                    if (currentItem == 0) {
                        post {
                            setCurrentItem(list.size - 2, false)
                        }
                    }
                }
            })
        }
    }
}