package com.jayce.vexis.foundation.ability.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.jayce.vexis.client.AndroidTool.parseMenu
import com.jayce.vexis.client.bean.MenuBean
import com.jayce.vexis.databinding.ActionWindowBinding
import com.jayce.vexis.databinding.WindowActionMenuBinding
import com.jayce.vexis.foundation.ui.block.ActionMenu
import java.util.concurrent.ConcurrentHashMap

object MenuManager {

    private val menuMap: ConcurrentHashMap<Int, List<MenuBean>> = ConcurrentHashMap()

    fun View.registerOnMenuClick(
        context: Context,
        menuId: Int,
        bundle: Bundle? = null,
        listener: OnMenuClick
    ) {
        val menuList = menuMap.getOrPut(menuId){
            parseMenu(context, menuId)
        }
        if (menuList.isEmpty()) return
        bindActionWindow(context, this, menuList, bundle, listener)
    }

    private fun bindActionWindow(
        context: Context,
        view: View,
        menuList: List<MenuBean>,
        bundle: Bundle?,
        listener: OnMenuClick
    ) {
        val inflater = LayoutInflater.from(context)
        val bind = ActionWindowBinding.inflate(inflater)
        val window = ActionMenu(bind.root, view)
        menuList.forEach { menu ->
            val textView = WindowActionMenuBinding.inflate(inflater, bind.menuContainer, false).root
            textView.text = menu.title
            textView.setOnClickListener {
                listener.onMenuItemClicked(menu.id, bundle)
                window.dismiss()
            }
            bind.menuContainer.addView(textView)
        }
        view.setOnLongClickListener {
            window.show()
            true
        }
    }
}