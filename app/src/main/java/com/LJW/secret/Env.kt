package com.LJW.secret

import android.content.Context

class Env {
    companion object{
        var applicationContext : Context? = null
            set(value) {
                field = value
            }
            get() = field?:SecretApplication.context.also { field = it }
    }
}