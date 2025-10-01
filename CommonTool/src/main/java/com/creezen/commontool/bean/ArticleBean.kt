package com.creezen.commontool.bean

data class ArticleBean(
    var articleId: Long = 0,
    var userId: String = "",
    var title: String = "",
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var favor: Long = 0
)
