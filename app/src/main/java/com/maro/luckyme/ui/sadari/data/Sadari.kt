package com.maro.luckyme.ui.sadari.data


data class Stream(
    val branchList: MutableList<Branch>? = mutableListOf()
)

data class Branch(
    val position: Int,
    val direction: Int // 0 왼쪽, 1 오른쪽
)