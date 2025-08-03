package com.jayce.vexis.core


import com.jayce.vexis.foundation.bean.UserEntry
import java.util.concurrent.atomic.AtomicReference

object SessionManager {

    private var onlineUserEntry = AtomicReference(UserEntry())

    fun registerUser(userEntry: UserEntry) {
        onlineUserEntry.set(userEntry)
    }

    fun user(): UserEntry {
        return onlineUserEntry.get()
    }
}