package com.ljw.secret.activities.pocket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ljw.secret.R
import android.util.Log

class NewRecord : AppCompatActivity() {

    private val userList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_pocket_record)
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

    }
}