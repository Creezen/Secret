package com.jayce.vexis.dynamic

import android.util.Log
import com.creezen.tool.ability.api.KitContract

class KitImpl : KitContract {

    override fun load() {
        Log.d("LJW", "Hello,this is lib")
    }

}