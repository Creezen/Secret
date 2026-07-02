package com.jayce.vexis.foundation.ability.menu

import android.os.Bundle

interface OnMenuClick {

    fun onMenuItemClicked(menuId: Int, bundle: Bundle?)
}