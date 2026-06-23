package com.jayce.vexis.business.kit

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.jayce.vexis.business.kit.digger.DiggerActivity
import com.jayce.vexis.business.kit.gomoku.GomokuActivity
import com.jayce.vexis.business.kit.book.BookActivity
import com.jayce.vexis.business.kit.maze.MazeActivity
import com.jayce.vexis.business.kit.pinyin.PinyinActivity
import com.jayce.vexis.business.kit.poker.PokerActivity
import com.jayce.vexis.business.kit.video.VideoPlayerActivity
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.WidgetsBinding
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import java.util.Locale

class KitFragment : BaseFragment<WidgetsBinding>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        showLocation()
        return binding.root
    }

    private fun initView() = binding.apply {
        ledger.setOnClickListener { jumpTo(BookActivity::class.java) }
        maze.setOnClickListener { jumpTo(MazeActivity::class.java) }
        gomoku.setOnClickListener { jumpTo(GomokuActivity::class.java) }
        poker.setOnClickListener { jumpTo(PokerActivity::class.java) }
        pinyin.setOnClickListener { jumpTo(PinyinActivity::class.java) }
        videoPlayer.setOnClickListener { jumpTo(VideoPlayerActivity::class.java) }
        mountDigger.setOnClickListener { jumpTo(DiggerActivity::class.java) }
        quickStart.setOnClickListener {
            jumpTo {
                val targetPackage = "com.DefaultCompany.Myproject"
                val targetClass = "com.unity3d.player.UnityPlayerActivity"
                val targetComponent = ComponentName(targetPackage, targetClass)
                component = targetComponent
            }
        }
    }

    private fun showLocation() {
        if (ActivityCompat.checkSelfPermission(activity as Context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        val locationManager = context?.getSystemService(LocationManager::class.java)
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f
        ) { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            val geocoder = Geocoder(activity as Context, Locale.CHINA)
            geocoder.getFromLocation(latitude, longitude, 1) {
                val address = it[0]
                val info = "${longitude}\t\t\t\t${latitude}\n" +
                    "${address.getAddressLine(0)}\n"
                binding.location.text = info
            }
        }
    }
}