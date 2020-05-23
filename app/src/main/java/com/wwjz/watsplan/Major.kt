package com.wwjz.watsplan

data class Major(
    val Communication1: List<String>? = listOf<String>(),
    val Communication2: List<String>? = listOf<String>(),
    val mFixed: List<String>? = listOf<String>(),
    val mFlexible: List<String>? = listOf<String>(),
    val sFixed: List<String>? = listOf<String>()
)