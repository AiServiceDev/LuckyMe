package com.maro.luckyme.ui.dice

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    fun getDiceCount() = 4
    fun getTotalUserCount() = 12
    fun getPenaltyWinningCount() = 1

    private val resultDataList = MutableLiveData<List<DiceUiData>>()
    private val currentDiceIndex = MutableLiveData<Int>()
    val currentDataList = Transformations.map(currentDiceIndex) {
        resultDataList.value?.subList(0, it)
    }

    val finishDiceRollingIndex = MutableLiveData<Int>()
    val scrollToEnd = MutableLiveData<Boolean>()
    val showResult = MutableLiveData<Boolean>()

    private val cacheFinishDiceIdList = mutableListOf<Int>()
    fun checkFinishDicesRolling(finishDiceId: Int) {
        cacheFinishDiceIdList.add(finishDiceId)
        if (getDiceCount() == cacheFinishDiceIdList.size) {
            cacheFinishDiceIdList.clear()
            nextDiceIndex()
        }
    }

    fun generatorNextDiceData() =
        mutableListOf<DiceUiData>()
            .apply {
                for (userIndex in 1..getTotalUserCount()) {
                    add(DiceUiData(VIEW_TYPE_NORMAL,
                        mutableListOf<DiceData>().apply {
                            for (diceCount in 1..getDiceCount()) {
                                add(
                                    DiceData(DiceStatusType.INIT).apply {
                                        initValue = Random.nextInt(IntRange(1, 6))
                                        resultValue = Random.nextInt(IntRange(1, 6))
                                    }
                                )
                            }
                        }
                    ))
                }
                resultDataList.postValue(this)
                currentDiceIndex.postValue(1)
            }


    fun getDiceVisibility(id: Int): Int {
        when (getDiceCount()) {
            1 -> {
                when (id) {
                    1 -> View.VISIBLE
                    else -> View.GONE
                }
            }
            2 -> {
                when (id) {
                    2, 3 -> View.VISIBLE
                    else -> View.GONE
                }
            }
            3 -> {
                when (id) {
                    1, 2, 3 -> View.VISIBLE
                    else -> View.GONE
                }
            }
            else -> View.VISIBLE
        }
        return View.VISIBLE
    }

    fun getDiceData(diceUiData: DiceUiData?, id: Int): DiceData? {
        return getValidCheckData(diceUiData, id - 1)
    }

    private fun getValidCheckData(diceUiData: DiceUiData?, dataIndex: Int): DiceData? {
        diceUiData?.let {
            val diceSize = it.diceDataList.size
            if (dataIndex in 0 until diceSize) {
                return it.diceDataList[dataIndex]
            }
        }
        return null
    }

    private fun nextDiceIndex() {
        if (resultDataList.value?.size ?: 0 > currentDiceIndex.value ?: 1) {
            currentDiceIndex.postValue(currentDiceIndex.value?.plus(1))
            scrollToEnd.postValue(true)
        }
    }
}