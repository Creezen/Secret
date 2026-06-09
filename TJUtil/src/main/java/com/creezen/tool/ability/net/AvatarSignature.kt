package com.creezen.tool.ability.net

import com.bumptech.glide.load.Key
import java.security.MessageDigest

class AvatarSignature(val msg: String) : Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(msg.toByteArray())
    }
}