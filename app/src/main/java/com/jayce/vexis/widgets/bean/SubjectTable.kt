package com.jayce.vexis.widgets.bean

data class SubjectTable (
    val discipline: List<String> = listOf(),
    val category: List<List<String>> = listOf(),
    val major: List<List<List<String>>> = listOf()
)