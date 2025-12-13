package com.jayce.vexis.dynamic

import android.os.Bundle
import android.view.View
import com.creezen.tool.ability.api.IActivity
import com.jayce.vexis.dynamic.databinding.ActivityLayoutBinding

class JumpActivity : IActivity() {

    private lateinit var binding: ActivityLayoutBinding

    override fun getView(): View {
        return binding.root
    }

    override fun onCreate(savedInstance: Bundle?) {
        getInflate()?.apply {
            binding = ActivityLayoutBinding.inflate(this)
        }
    }
}