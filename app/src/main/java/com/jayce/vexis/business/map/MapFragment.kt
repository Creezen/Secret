package com.jayce.vexis.business.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.jayce.vexis.R
import com.jayce.vexis.client.TLog
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.ActivityMapBinding

class MapFragment : BaseFragment<ActivityMapBinding>(), AMapLocationListener {

    private var aMap: AMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding.map.onCreate(savedInstanceState)
        init(binding.map)
        return binding.root
    }

    private fun init(map: MapView) {
        aMap = map.map
        initMap()
    }

    private fun initMap() {
        initAMap()
        initLocation()
        initSetting()
        drawAMap()
    }

    private fun initAMap() {
        val style = MyLocationStyle().apply {
            interval(500L)
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            showMyLocation(true)
            activity?.getColor(R.color.transparent)?.let { radiusFillColor(it) }
        }
        aMap?.isMyLocationEnabled = true
        aMap?.myLocationStyle = style
        aMap?.showIndoorMap(true)
    }

    private fun initSetting() {
        val settings = aMap?.uiSettings ?: return
        settings.apply {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = true
            isScaleControlsEnabled = true
            isTiltGesturesEnabled = false
        }
    }

    private fun initLocation() {
        val locationClient = AMapLocationClient(this.activity)
        val option = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            interval = 500
            isNeedAddress = true
        }
        locationClient.setLocationOption(option)
//        locationClient.setLocationListener(this)
        locationClient.startLocation()

        val latLng = LatLng(28.012019846526506, 115.50436166973468)
        val cameraOption = CameraPosition(latLng, 16f, 0f, 0f)
        aMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraOption))
    }

    private fun drawAMap() {
        val latLng = LatLng(28.012019846526506, 115.50436166973468)
        aMap?.addMarker(MarkerOptions().position(latLng).title("北京").snippet("标记"))
    }

    override fun onLocationChanged(location: AMapLocation?) {
        if (location == null) return
        TLog.d("location: ${location.address} -- ${location.latitude}  --  ${location.longitude}")
    }
}