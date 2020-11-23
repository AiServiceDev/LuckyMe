package com.maro.luckyme.ui.jebi

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maro.luckyme.data.common.CommonData
import com.maro.luckyme.domain.jebi.GetJebiParam
import com.maro.luckyme.domain.jebi.GetJebiUseCase
import com.maro.luckyme.domain.jebi.LuckyRepository
import com.maro.luckyme.domain.Result
import com.maro.luckyme.domain.jebi.GetJebiFlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class JebiViewModel : ViewModel() {

    private val getJebiUseCase = GetJebiUseCase(Dispatchers.IO)
    private val getJebiFlowUseCase = GetJebiFlowUseCase(Dispatchers.IO)

    // 밋밋한 버전
    val param = MutableLiveData<Pair<Int, Int>>()
    val result = map(param) {
        val repository = LuckyRepository()
        repository.makeResult(it.first, it.second)
    }
    // Coroutine 버전
    var paramCoroutine: Pair<Int, Int>? = null
    // Flow 버전
    var paramFlow: Pair<Int, Int>? = null
    // 결과
    val items = MediatorLiveData<List<JebiItem>>()
    val winnings = map(items) { items -> items.filter { it.winning } }
    val selected = MutableLiveData<JebiItem>()

    init {
        items.addSource(result) {
            items.value = when (it) {
                is Result.Success -> mutableListOf<JebiItem>().apply {
                    Timber.d("[sunchulbaek] 당첨 = ${it.data.first}")
                    Timber.d("[sunchulbaek] 전체 = ${it.data.second}")
                    it.data.second.forEachIndexed { index, shuffledIndex ->
                        add(JebiItem(CommonData.get12KanjiListByIndex(shuffledIndex), it.data.first.contains(index)))
                    }
                }
                is Result.Error -> TODO()
            }
        }
    }

    // 밋밋한 버전
    fun initPlain(winning: Int, total: Int) {
        param.value = Pair(winning, total)
    }

    // 코루틴 버전
    fun initCoroutine(winning: Int, total: Int) = viewModelScope.launch(Dispatchers.Main) {
        paramCoroutine = Pair(winning, total)
        Timber.d("[sunchulbaek] initCoroutine(1) ${Thread.currentThread()}")
        when (val result = getJebiUseCase(GetJebiParam(winning, total))) {
            is Result.Success -> {
                Timber.d("[sunchulbaek] initCoroutine(2) ${Thread.currentThread()}")
                items.value = result.data
            }
            is Result.Error -> TODO()
        }
    }

    // Flow 버전
    fun initFlow(winning: Int, total: Int) = viewModelScope.launch(Dispatchers.Main) {
        paramFlow = Pair(winning, total)
        Timber.d("[sunchulbaek] initFlow(1) ${Thread.currentThread()}")
        getJebiFlowUseCase(GetJebiParam(winning, total)).collect {
            when (it) {
                is Result.Success -> {
                    Timber.d("[sunchulbaek] initFlow(2) ${Thread.currentThread()}")
                    items.value = it.data
                }
                is Result.Error -> TODO()
            }
        }
    }

    fun refresh() = diff()

    fun minusTotal() = diff(total = -1)

    fun plusTotal() = diff(total = 1)

    fun minusWinning() = diff(winning = -1)

    fun plusWinning() = diff(winning = 1)

    fun diff(winning: Int? = null, total: Int? = null): Any = when {
        paramFlow != null -> { // Flow 버전
            initFlow(paramFlow!!.first + (winning ?: 0), paramFlow!!.second + (total ?: 0))
        }
        paramCoroutine != null -> { // Coroutine 버전
            initCoroutine(paramCoroutine!!.first + (winning ?: 0), paramCoroutine!!.second + (total ?: 0))
        }
        else -> { // 밋밋한 버전
            param.value = Pair(param.value!!.first + (winning ?: 0), param.value!!.second + (total ?: 0))
        }
    }

    fun select(index: Int) {
        items.value = items.value?.mapIndexed { index2, item ->
            if (index == index2) {
                JebiItem(item.icon, item.winning, true).apply {
                    this@JebiViewModel.selected.value = this
                }
            } else item
        }
    }

    fun confirm() {
        selected.value = selected.value?.apply {
            result = if (this.winning) "꽝" else "통과"
        }
    }

    fun close() {
        selected.value = null
    }
}