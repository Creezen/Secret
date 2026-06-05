package com.jayce.vexis.business.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.ActivityMapBinding

class MapFragment : BaseFragment<ActivityMapBinding>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding.map.onCreate(savedInstanceState)
        return binding.root
    }
}