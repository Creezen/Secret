package com.jayce.vexis.core

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class CreezenAccessibility : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {}
            else -> {}
        }
    }

    override fun onInterrupt() { /**/ }

    override fun onServiceConnected() { /**/ }
}