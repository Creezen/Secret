package com.jayce.vexis.exchange

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.R
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityHubBinding
import com.jayce.vexis.exchange.Senior.Senior
import com.jayce.vexis.exchange.media.MediaLibraryActivity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ExchangeActivity : BaseActivity() {
    private lateinit var binding: ActivityHubBinding
    private val fragmentList = arrayListOf<Fragment>()
    private val exchangeAdapter by lazy {
        ExchangeAdapter(supportFragmentManager, lifecycle, fragmentList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initPage()
    }

    private fun initPage() {
        with(binding) {
            page.adapter = exchangeAdapter
            TabLayoutMediator(tab, page) { tab, pos ->
                val textView = TextView(this@ExchangeActivity).apply {
                    gravity = Gravity.CENTER
                }
                textView.text = when(pos) {
                    0 -> getString(R.string.knowledge_share)
                    1 -> getString(R.string.resource_share)
                    else -> ""
                }
                tab.customView = textView
            }.attach()
        }
    }

    private fun initData() {
        fragmentList.add(Senior())
        fragmentList.add(MediaLibraryActivity())
    }
}
