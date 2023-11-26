package com.ljw.secret.Activities.SubModule

import android.os.Bundle
import com.ljw.secret.Activities.BaseActivity
import com.ljw.secret.databinding.PocketBookBinding
class PocketBook: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=PocketBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}