package com.maro.luckyme.ui.dice.data

import com.maro.luckyme.ui.dice.DiceViewModel.DiceStatusType

/**
 * 시작 되면 값도 랜덤으로 바뀌어서 보여지도록 하자.
 */
data class DiceData(
    val type: DiceStatusType = DiceStatusType.INIT,
    var initValue: Int = -1,
    var resultValue: Int = -1
)