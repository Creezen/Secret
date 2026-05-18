package com.jayce.vexis.foundation.ability.socket

interface InnerLanStatusListener {

    fun onToast(text: String)

    fun onActionChange(cancelLabel: String, confirmLabel: String)

    fun onStageChange(stage: Int)
}