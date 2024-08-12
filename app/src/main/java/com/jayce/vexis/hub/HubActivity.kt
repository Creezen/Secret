package com.jayce.vexis.hub

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.R
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityHubBinding
import com.jayce.vexis.hub.resource.ResourceLibraryActivity
import com.jayce.vexis.hub.share.Sage

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class HubActivity : BaseActivity() {

    private lateinit var binding: ActivityHubBinding
    private val fragmentList = arrayListOf<Fragment>()
    private val hubAdapter by lazy {
        HubAdapter(supportFragmentManager, lifecycle, fragmentList)
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
            page.adapter = hubAdapter
            TabLayoutMediator(tab, page){ tab, pos ->
                val textView = TextView(this@HubActivity)
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
        fragmentList.add(Sage())
        fragmentList.add(ResourceLibraryActivity())
    }

}