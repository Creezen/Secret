package com.creezen.commontool.bean

import com.creezen.commontool.Config.LONG_ZERO
import com.creezen.commontool.Config.NIL

data class SectionBean (
    var sectionId: Long = LONG_ZERO,
    var articleId: Long = LONG_ZERO,
    var content: String = NIL
)