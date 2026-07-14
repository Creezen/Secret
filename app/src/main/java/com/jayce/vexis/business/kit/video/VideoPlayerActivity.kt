package com.jayce.vexis.business.kit.video

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.jayce.vexis.client.TLog
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : BaseActivity<ActivityVideoPlayerBinding>(), Player.Listener {

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
        videoPlayer?.apply {
            addListener(this@VideoPlayerActivity)
            seekTo(5000L)
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.play()
        binding.player.onResume()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer?.pause()
        binding.player.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer?.release()
        videoPlayer = null
    }

    private fun playVideo(uri: Uri) {
        val item = MediaItem.fromUri(uri)
        videoPlayer?.setMediaItem(item)
        videoPlayer?.addMediaItem(item)
        videoPlayer?.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        TLog.d("player state change: $playbackState")
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        TLog.d("time line: ${videoPlayer?.currentPosition}  ${videoPlayer?.currentLiveOffset}")
    }
}