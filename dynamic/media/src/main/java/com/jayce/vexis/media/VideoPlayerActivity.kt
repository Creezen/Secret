package com.jayce.vexis.media

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ability.api.IActivity
import com.jayce.vexis.media.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : IActivity<Array<String>, Uri?>(), Player.Listener {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var videoPlayer: ExoPlayer? = null

    override fun getContract() = openFile()

    override fun onContractCallback(data: Uri?) {
        if (data == null) return
        playVideo(data)
    }

    override fun getView() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        getInflate()?.apply {
            binding = ActivityVideoPlayerBinding.inflate(this)
        }
        initPlayer()
        launcher?.launch(arrayOf("video/*"))
    }

    private fun initPlayer() {
        videoPlayer = ExoPlayer.Builder(contextWrapper).build()
        videoPlayer?.playWhenReady = true
        binding.player.player = videoPlayer
        binding.player.setFullscreenButtonClickListener {
            (host as Activity).requestedOrientation = if (it) SCREEN_ORIENTATION_LANDSCAPE else SCREEN_ORIENTATION_PORTRAIT
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