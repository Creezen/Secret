package com.jayce.vexis.ai

import android.os.Bundle
import android.view.View
import com.jayce.vexis.ai.databinding.ActivityLayoutBinding
import com.jayce.vexis.client.ability.api.IActivity

class JumpActivity : IActivity<Any, Any>() {

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