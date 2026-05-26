package com.jayce.vexis.business.mail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MailAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    val list: List<Fragment>
) : FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount() = list.size

    override fun createFragment(position: Int) = list[position]
}