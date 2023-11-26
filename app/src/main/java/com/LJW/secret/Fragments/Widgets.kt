package com.ljw.secret.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ljw.secret.Activities.SubModule.MultiThread
import com.ljw.secret.Activities.SubModule.PocketBook
import com.ljw.secret.databinding.WidgetsBinding

class Widgets:BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding=WidgetsBinding.inflate(inflater)
        with(binding){
            pocketBook.setOnClickListener { startActivity(Intent(context, PocketBook::class.java)) }
            multiThread.setOnClickListener { startActivity(Intent(context, MultiThread::class.java)) }
        }
        return binding.root
    }
}
