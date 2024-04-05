package com.ljw.secret.activities.pocket

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ljw.secret.databinding.NewPocketRecordBinding

class NewRecord : AppCompatActivity() {

    private lateinit var binding: NewPocketRecordBinding
    private val userList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewPocketRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        Log.e("NewRecord.onCreate","$userList")
    }

    private fun initData() {
        userList.clear()
        intent.getStringArrayListExtra("userData")?.let {
            userList.addAll(
                it.filter {
                    it.isNotEmpty()
                }.toSet()
            )
        }
    }

    private fun initView() {
        binding.titleTv.setText(intent.getStringExtra("title"))
    }
}