package com.jayce.vexis.business.member.dashboard

import com.bumptech.glide.load.Key
import java.security.MessageDigest

class AvatarSignnature(val msg: String) : Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(msg.toByteArray())
    }
}
