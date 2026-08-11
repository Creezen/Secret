package com.jayce.vexis.domain.bo

data class SubjectBO(
    val discipline: List<String> = listOf(),
    val category: List<List<String>> = listOf(),
    val major: List<List<List<String>>> = listOf()
)