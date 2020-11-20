package com.maro.luckyme.domain.jebi

import com.maro.luckyme.domain.FlowUseCase
import com.maro.luckyme.domain.Result
import com.maro.luckyme.ui.jebi.JebiItem
import com.maro.luckyme.ui.jebi.JebiViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetJebiFlowUseCase(
    private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetJebiParam, List<JebiItem>>(coroutineDispatcher) {

    private val repository = LuckyRepository()

    override fun execute(parameters: GetJebiParam): Flow<Result<List<JebiItem>>> = flow {
        val ret = mutableListOf<JebiItem>()
        val result = repository.makeResultFlow(parameters.winning, parameters.total)
        Timber.d("[sunchulbaek] 당첨 = ${result.first}")
        Timber.d("[sunchulbaek] 전체 = ${result.second}")
        result.second.forEachIndexed { index, shuffledIndex ->
            ret.add(JebiItem(JebiViewModel.icons[shuffledIndex], result.first.contains(index)))
        }
        emit(Result.Success(ret))
    }
}