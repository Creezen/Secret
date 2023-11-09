package com.LJW.secret.Activities.SubModule

import android.os.Bundle
import com.LJW.secret.Activities.BaseActivity
import com.LJW.secret.databinding.PocketBookBinding
class PocketBook: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=PocketBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}