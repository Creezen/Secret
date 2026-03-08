package com.jayce.vexis.domain.bean

data class SubjectTableEntry (
    val discipline: List<String> = listOf(),
    val category: List<List<String>> = listOf(),
    val major: List<List<List<String>>> = listOf()
)