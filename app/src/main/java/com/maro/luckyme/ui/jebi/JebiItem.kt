package com.maro.luckyme.ui.jebi

data class JebiItem(
    val icon: Int,
    val selected: Boolean = false,
    var result: String? = null
)