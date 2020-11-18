package com.maro.luckyme.ui.dice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maro.luckyme.ui.dice.DiceResultAdapter.Companion.VIEW_TYPE_NORMAL
import com.maro.luckyme.ui.dice.data.DiceData
import com.maro.luckyme.ui.dice.data.DiceUiData
import kotlin.random.Random
import kotlin.random.nextInt

class DiceViewModel : ViewModel() {

    enum class DiceStatusType {
        INIT,
        SHUFFLE,
        RESULT
    }

    fun getDiceCount() = 2
    fun getTotalUserCount() = 6
    fun getPenaltyWinningCount() = 1

    val dataList = MutableLiveData<List<DiceUiData>>()

    fun generatorNextDiceData() =
        mutableListOf<DiceUiData>()
            .apply {
                for (userIndex in 1..getTotalUserCount()) {
                    add(DiceUiData(VIEW_TYPE_NORMAL,
                        mutableListOf<DiceData>().apply {
                            for (diceCount in 1..getDiceCount()) {
                                add(
                                    DiceData(DiceStatusType.INIT).apply {
                                        Random(System.currentTimeMillis()).apply {
                                            initValue = nextInt(IntRange(1, 6))
                                            resultValue = nextInt(IntRange(1, 6))
                                        }
                                    }
                                )
                            }
                        }
                    ))
                }
                dataList.postValue(this)
            }
}