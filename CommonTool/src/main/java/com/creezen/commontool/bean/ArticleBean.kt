package com.creezen.commontool.bean

//TODO  article section remark
data class ArticleBean(
    var articleID: Long = 0,
    var userID: String = "",
    var title: String = "",
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var favor: Long = 0
)
