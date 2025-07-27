package com.jayce.vexis.business.peer

data class SubjectTable (
    val discipline: List<String> = listOf(),
    val category: List<List<String>> = listOf(),
    val major: List<List<List<String>>> = listOf()
)