package com.creezen.commontool.bean

import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.commontool.Config.Constant.LONG_0

data class SectionBean (
    var sectionId: Long = LONG_0,
    var articleId: Long = LONG_0,
    var content: String = EMPTY_STRING
)