package com.creezen.commontool.bean

import com.creezen.commontool.Config.NIL

data class ArticleBean(
    var articleId: Long = 0,
    var userId: String = NIL,
    var title: String = NIL,
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var favor: Long = 0
)
