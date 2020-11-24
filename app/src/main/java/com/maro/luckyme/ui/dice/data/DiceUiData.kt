package com.maro.luckyme.ui.dice.data

import com.maro.luckyme.data.common.UserUiData


/**
 */
data class DiceUiData(
    var viewType: Int,
    val diceDataList: List<DiceData>
) {
    fun getSumResult() =
        diceDataList.sumOf { it.resultValue }.toString()
}