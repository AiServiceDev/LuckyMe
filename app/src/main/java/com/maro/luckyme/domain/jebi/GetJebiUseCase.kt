package com.maro.luckyme.domain.jebi

import com.maro.luckyme.data.common.CommonData
import com.maro.luckyme.domain.UseCase
import com.maro.luckyme.ui.jebi.JebiItem
import com.maro.luckyme.ui.jebi.JebiViewModel
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber

class GetJebiParam(
    val winning: Int,
    val total: Int
)

class GetJebiUseCase(
    private val coroutineDispatcher: CoroutineDispatcher
) : UseCase<GetJebiParam, List<JebiItem>>(coroutineDispatcher) {

    private val repository = LuckyRepository()

    override suspend fun execute(parameters: GetJebiParam): List<JebiItem> {
        val ret = mutableListOf<JebiItem>()
        val result = repository.makeResultCoroutine(parameters.winning, parameters.total)
        Timber.d("[sunchulbaek] 당첨 = ${result.first}")
        Timber.d("[sunchulbaek] 전체 = ${result.second}")
        result.second.forEachIndexed { index, shuffledIndex ->
            ret.add(JebiItem(CommonData.get12KanjiListByIndex(shuffledIndex), result.first.contains(index)))
        }
        return ret
    }
}