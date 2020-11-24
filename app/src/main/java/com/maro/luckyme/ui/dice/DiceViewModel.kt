package com.maro.luckyme.ui.dice

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.maro.luckyme.ui.dice.data.DiceData
import com.maro.luckyme.ui.dice.data.DiceUiData
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextInt

class DiceViewModel : ViewModel() {

    enum class DiceStatusType {
        INIT,
        SHUFFLE,
        RESULT
    }

    companion object {
        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_CURRENT = 1
        const val VIEW_TYPE_PENALTY_WINNING = 2
    }

    val diceCount = MutableLiveData<Int>()
    val totalUserCount = MutableLiveData<Int>()
    val penaltyWinningCount = MutableLiveData<Int>()

    //false 이면 작은 수 당첨 , true 큰수 당첨.
    val gameRule = MutableLiveData<Boolean>().apply {
        value = false
    }

    val currentRandomSumText = MutableLiveData<String>()
    private val resultDataList = MutableLiveData<List<DiceUiData>>()
    val scrollToEnd = MutableLiveData<Boolean>()
    val showResult = MutableLiveData<Boolean>()
    val retry = MutableLiveData<Boolean>()
    val finishAllDiceRollingUpdate = MutableLiveData<Boolean>().apply { value = false }

    /**
     * 1 부터 시작.
     */
    val currentDiceIndex = MutableLiveData<Int>()
    val currentDataList = Transformations.map(currentDiceIndex) {
        if (it <= 0) return@map mutableListOf<DiceUiData>()
        var subList = resultDataList.value?.subList(0, it)
        return@map if (gameRule.value == false) {
            subList
                ?.map { diceUiData ->
                    diceUiData.viewType = VIEW_TYPE_NORMAL
                    diceUiData
                }
                ?.sortedBy { diceUiData -> diceUiData.getSumResult() }
                ?.take(penaltyWinningCount.value ?: 1)?.map { diceUiData ->
                    diceUiData.viewType = VIEW_TYPE_PENALTY_WINNING
                    diceUiData
                }
            subList
        } else {
            subList
                ?.map { diceUiData ->
                    diceUiData.viewType = VIEW_TYPE_NORMAL
                    diceUiData
                }
                ?.sortedByDescending { diceUiData -> diceUiData.getSumResult() }
                ?.take(penaltyWinningCount.value ?: 1)?.map { diceUiData ->
                    diceUiData.viewType = VIEW_TYPE_PENALTY_WINNING
                }
            subList
        }
    }


    private val cacheFinishDiceIdList = mutableListOf<Int>()
    fun checkFinishDicesRolling(finishDiceId: Int) {
        cacheFinishDiceIdList.add(finishDiceId)
        if (diceCount.value ?: 0 == cacheFinishDiceIdList.size) {
            cacheFinishDiceIdList.clear()
            nextDiceIndex()
        }
    }

    fun generatorNextDiceData() =
        mutableListOf<DiceUiData>()
            .apply {
                val userCount = totalUserCount.value ?: 1
                val generatorPenaltyWinnerIndexList = generatorPenaltyWinnerIndexList(userCount)
                for (userIndex in 1..userCount) {
                    add(
                        DiceUiData(
                            VIEW_TYPE_NORMAL,
                            getSequencedRandomDice(
                                generatorPenaltyWinnerIndexList.contains(
                                    userIndex
                                )
                            )
                        )
                    )
                }
                finishAllDiceRollingUpdate.value = false
                resultDataList.postValue(this)
                currentDiceIndex.postValue(1)
                startRandomDiceSumText()
            }

    /**
     * 시작시 당첨된 유저를 랜덤하게 를 결정해 준다.
     */
    private fun generatorPenaltyWinnerIndexList(userCount: Int): ArrayList<Int> {
        val penaltyWinnerCount = penaltyWinningCount.value ?: 1
        val penaltyWinnerIndexList = arrayListOf<Int>()
        do {
            var select: Int
            do {
                select = Random.nextInt(IntRange(1, userCount))
            } while (penaltyWinnerIndexList.binarySearch(select) >= 0)
            penaltyWinnerIndexList.add(select)
        } while (penaltyWinnerIndexList.size < penaltyWinnerCount)
        return penaltyWinnerIndexList
    }

    private fun getSequencedRandomDice(penaltyWinner: Boolean): MutableList<DiceData> {
        return mutableListOf<DiceData>().apply {
            for (diceCount in 1..(diceCount.value ?: 1)) {
                if (penaltyWinner) {
                    if (gameRule.value == true) {
                        add(
                            DiceData(DiceStatusType.INIT).apply {
                                initValue = Random.nextInt(IntRange(1, 6))
                                resultValue = Random.nextInt(IntRange(4, 6))
                            }
                        )
                    } else {
                        add(
                            DiceData(DiceStatusType.INIT).apply {
                                initValue = Random.nextInt(IntRange(1, 6))
                                resultValue = Random.nextInt(IntRange(1, 3))
                            }
                        )
                    }
                } else {
                    if (gameRule.value == true) {
                        add(
                            DiceData(DiceStatusType.INIT).apply {
                                initValue = Random.nextInt(IntRange(1, 6))
                                resultValue = Random.nextInt(IntRange(1, 3))
                            }
                        )
                    } else {
                        add(
                            DiceData(DiceStatusType.INIT).apply {
                                initValue = Random.nextInt(IntRange(1, 6))
                                resultValue = Random.nextInt(IntRange(4, 6))
                            }
                        )
                    }
                }
            }
        }
    }


    fun getDiceVisibility(id: Int): Int =
        when (diceCount.value) {
            1 -> {
                when (id) {
                    1 -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            }
            2 -> {
                when (id) {
                    2, 3 -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            }
            3 -> {
                when (id) {
                    1, 2, 3 -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            }
            else -> View.VISIBLE
        }

    fun getDiceData(diceUiData: DiceUiData?, id: Int): DiceData? {
        return getValidCheckData(diceUiData, id)
    }

    private fun getValidCheckData(diceUiData: DiceUiData?, id: Int): DiceData? {
        diceUiData?.let {
            val diceSize = it.diceDataList.size
            val rearrangeIndex = when (diceCount.value) {
                1 -> {
                    when (id) {
                        1 -> 0
                        else -> 0
                    }
                }
                2 -> {
                    when (id) {
                        2, 3 -> id - 2
                        else -> 0
                    }
                }
                3 -> {
                    when (id) {
                        1, 2, 3 -> id - 1
                        else -> 0
                    }
                }
                else -> id - 1
            }
            if (rearrangeIndex in 0 until diceSize) {
                return it.diceDataList[rearrangeIndex]
            }
        }
        return null
    }

    private fun nextDiceIndex() {
        if (resultDataList.value?.size ?: 0 > currentDiceIndex.value ?: 1) {
            currentDiceIndex.postValue(currentDiceIndex.value?.plus(1))
            scrollToEnd.postValue(true)
            startRandomDiceSumText()
        } else {
            stopRandomDiceSumText()
            finishAllDiceRollingUpdate.postValue(true)
        }
    }


    private var diceSumTextAnimation: ValueAnimator? = null
    private var cacheFraction: Int? = null
    private fun startRandomDiceSumText() {
        diceSumTextAnimation?.cancel()
        diceSumTextAnimation = ValueAnimator().apply {
            setIntValues(0, 1)
            duration = 1900
            repeatCount = 0
            interpolator = FastOutSlowInInterpolator()
            removeAllUpdateListeners()
            removeAllListeners()
            addUpdateListener {
                if (cacheFraction != (it.animatedFraction * 100f).toInt()) {
                    cacheFraction = (it.animatedFraction * 100f).toInt()
                    val diceCount = diceCount.value ?: 2
                    currentRandomSumText.postValue(
                        Random.nextInt(
                            IntRange(
                                diceCount,
                                diceCount * 6
                            )
                        ).toString()
                    )
                }
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    currentDiceIndex.value?.let {
                        currentRandomSumText.postValue(
                            resultDataList.value?.get(it - 1)?.getSumResult() ?: "0"
                        )
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                    currentDiceIndex.value?.let {
                        currentRandomSumText.postValue(
                            resultDataList.value?.get(it - 1)?.getSumResult() ?: "0"
                        )
                    }
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    private fun stopRandomDiceSumText() {
        if (diceSumTextAnimation?.isRunning == true) diceSumTextAnimation?.end()
    }

    private fun clearAnimation() {
        diceSumTextAnimation?.cancel()
        diceSumTextAnimation?.run {
            removeAllUpdateListeners()
            removeAllListeners()
        }
    }

    override fun onCleared() {
        clearAnimation()
        super.onCleared()
    }

    fun retry() {
        currentDiceIndex.postValue(0)
        retry.postValue(true)
    }

    fun showResult() {
        clearAnimation()
        resultDataList.value?.forEach {
            it.diceDataList.forEach {
                it.type = DiceStatusType.RESULT
            }
        }
        currentDiceIndex.postValue(totalUserCount.value)
        finishAllDiceRollingUpdate.value = true
        showResult.postValue(true)
    }
}