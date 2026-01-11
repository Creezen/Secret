package com.creezen.tool.bean

import androidx.annotation.AnimRes

data class FragmentAnimRes (
    @AnimRes val enterAnim: Int = -1,
    @AnimRes val exitAnim: Int = -1,
    @AnimRes val popEnterAnim: Int = -1,
    @AnimRes val popExitAnim: Int = -1
)