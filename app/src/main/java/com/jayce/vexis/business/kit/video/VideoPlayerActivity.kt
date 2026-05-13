package com.jayce.vexis.business.kit.video

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : BaseActivity<ActivityVideoPlayerBinding>() {

    private var videoPlayer: ExoPlayer? = null
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        super.registerLauncher()
        launcher = getLauncher(openFile()) {
            if (it == null) return@getLauncher
            playVideo(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initPlayer()
        launcher?.launch(arrayOf("video/*"))
    }

    private fun initPlayer() {
        videoPlayer = ExoPlayer.Builder(this).build()
        videoPlayer?.playWhenReady = true
        binding.player.player = videoPlayer
        binding.player.setFullscreenButtonClickListener {
            requestedOrientation = if (it) SCREEN_ORIENTATION_LANDSCAPE else SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun playVideo(uri: Uri) {
        val item = MediaItem.fromUri(uri)
        videoPlayer?.setMediaItem(item)
        videoPlayer?.prepare()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.play()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer?.release()
        videoPlayer = null
    }
}