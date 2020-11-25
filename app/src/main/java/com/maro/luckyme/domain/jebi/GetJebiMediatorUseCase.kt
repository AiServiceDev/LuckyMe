package com.maro.luckyme.domain.jebi

import com.maro.luckyme.data.common.CommonData
import com.maro.luckyme.domain.MediatorUseCase
import com.maro.luckyme.domain.Result
import com.maro.luckyme.ui.jebi.JebiItem
import timber.log.Timber

class GetJebiMediatorUseCase : MediatorUseCase<GetJebiParam, List<JebiItem>>() {

    private val repository = LuckyRepository()

    override fun execute(parameters: GetJebiParam) {
        val ret = mutableListOf<JebiItem>()
        val result = repository.makeResult(parameters.winning, parameters.total)
        Timber.d("[sunchulbaek] 당첨 = ${result.first}")
        Timber.d("[sunchulbaek] 전체 = ${result.second}")
        result.second.forEachIndexed { index, shuffledIndex ->
            ret.add(JebiItem(CommonData.get12KanjiListByIndex(shuffledIndex), result.first.contains(index)))
        }
        this@GetJebiMediatorUseCase.result.postValue(Result.Success(ret))
    }
}