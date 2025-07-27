package com.creezen.tool.bean

data class InitParam(
    val socketPort: Int = 0,
    val baseSocketPath: String = "",
    val baseUrl: String = "",
    val apiBaseUrl: String = ""
)