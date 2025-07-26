package com.jayce.vexis.core


import com.jayce.vexis.business.member.User
import java.util.concurrent.atomic.AtomicReference

object SessionManager {

    private var onlineUser = AtomicReference(User())

    fun registerUser(user: User) {
        onlineUser.set(user)
    }

    fun user(): User {
        return onlineUser.get()
    }
}