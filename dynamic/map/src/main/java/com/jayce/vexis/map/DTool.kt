package com.jayce.vexis.map

import com.amap.api.services.core.ServiceSettings
import com.jayce.vexis.client.ability.api.ITool

class DTool : ITool() {

    override fun init() {
        ServiceSettings.updatePrivacyAgree(context, true)
        ServiceSettings.updatePrivacyShow(context, true, true)
//        MapsInitializer.setTerrainEnable(true)
    }
}