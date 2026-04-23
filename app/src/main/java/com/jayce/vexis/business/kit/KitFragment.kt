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
import com.jayce.vexis.business.kit.gomoku.GomokuActivity
import com.jayce.vexis.business.kit.ledger.LedgerSheetActivity
import com.jayce.vexis.business.kit.maze.MazeActivity
import com.jayce.vexis.business.kit.pinyin.PinyinActivity
import com.jayce.vexis.business.kit.poker.PokerActivity
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.WidgetsBinding
import java.util.Locale

class KitFragment : BaseFragment<WidgetsBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initView()
        showLocation()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            ledger.setOnClickListener {
                startActivity(Intent(context, LedgerSheetActivity::class.java))
            }
            maze.setOnClickListener {
                startActivity(Intent(context, MazeActivity::class.java))
            }
            gomoku.setOnClickListener {
                startActivity(Intent(context, GomokuActivity::class.java))
            }
            poker.setOnClickListener {
                startActivity(Intent(context, PokerActivity::class.java))
            }
            pinyin.setOnClickListener {
                startActivity(Intent(context, PinyinActivity::class.java))
            }
            quickStart.setOnClickListener {
                val component = ComponentName("com.DefaultCompany.Myproject", "com.unity3d.player.UnityPlayerActivity")
                val intent = Intent()
                intent.component = component
                startActivity(intent)
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
                val info = "${longitude}\n" +
                        "${latitude}\n" +
                        "${address.getAddressLine(0)}\n" +
                        "${address.getAddressLine(1)}\n" +
                        "${address.getAddressLine(2)}"
                binding.location.text = info
            }
        }
    }

}