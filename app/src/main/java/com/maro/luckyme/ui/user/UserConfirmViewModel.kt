package com.maro.luckyme.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.maro.luckyme.data.common.UserUiData

class UserConfirmViewModel : ViewModel() {
    companion object {
        const val DEFAULT_DICE_COUNT = 2
        const val MIN_DICE_COUNT = 1
        const val MAX_DICE_COUNT = 4

        const val DEFAULT_USER_COUNT = 4
        const val DEFAULT_PENALTY_WINNER_COUNT = 1
        const val MAX_USER_COUNT = 12
        const val MIN_USER_COUNT = 2
        const val MIN_PENALTY_WINNER_USER_COUNT = 1
    }

    var totalUserCount = MutableLiveData<Int>().apply { value = DEFAULT_USER_COUNT }
    var penaltyWinnerCount = MutableLiveData<Int>().apply { value = DEFAULT_PENALTY_WINNER_COUNT }
    var diceCount = MutableLiveData<Int>().apply { value = DEFAULT_DICE_COUNT }
    var start = MutableLiveData<Boolean>()

    //false 이면 작은 수 당첨 , true 큰수 당첨.
    val gameRule = MutableLiveData<Boolean>().apply {
        value = false
    }

    val userDataList = totalUserCount.map {
        mutableListOf<UserUiData>().apply {
            for (index in 0 until it) {
                add(UserUiData(index))
            }
        }
    }

    fun plusUserCount() = totalUserCount.value?.apply {
        if (this < MAX_USER_COUNT) {
            totalUserCount.value = plus(1)
        }
    }

    fun minusUserCount() = totalUserCount.value?.apply {
        if (this > MIN_USER_COUNT) {
            val changeValue = minus(1)
            totalUserCount.value = changeValue
            if (penaltyWinnerCount.value ?: 1 >= changeValue) {
                penaltyWinnerCount.value = changeValue - 1
            }
        }
    }

    fun plusPenaltyWinnerCount() =
        penaltyWinnerCount.value?.apply {
            if (this < (totalUserCount.value ?: DEFAULT_USER_COUNT) - 1) {
                penaltyWinnerCount.value = plus(1)
            }
        }

    fun minusPenaltyWinnerCount() =
        penaltyWinnerCount.value?.apply {
            if (this > MIN_PENALTY_WINNER_USER_COUNT) {
                penaltyWinnerCount.value = minus(1)
            }
        }

    fun plusDiceCount() = diceCount.value?.apply {
        if (this < MAX_DICE_COUNT) {
            diceCount.value = plus(1)
        }
    }

    fun minusDiceCount() = diceCount.value?.apply {
        if (this > MIN_DICE_COUNT) {
            diceCount.value = minus(1)
        }
    }


    fun start() {
        start.postValue(true)
    }
}