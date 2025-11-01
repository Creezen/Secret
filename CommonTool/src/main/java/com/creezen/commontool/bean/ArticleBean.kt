package com.creezen.commontool.bean

import com.creezen.commontool.Config.Constant.EMPTY_STRING

data class ArticleBean(
    var articleId: Long = 0,
    var userId: String = EMPTY_STRING,
    var title: String = EMPTY_STRING,
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var favor: Long = 0
)
