package com.maro.luckyme.ui.jebi

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maro.luckyme.domain.Result
import com.maro.luckyme.domain.jebi.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class JebiViewModel : ViewModel() {

    private val getJebiMediatorUseCase = GetJebiMediatorUseCase()
    private val getJebiUseCase = GetJebiUseCase(Dispatchers.IO)
    private val getJebiFlowUseCase = GetJebiFlowUseCase(Dispatchers.IO)

    // MediatorUseCase 버전
    var param: Pair<Int, Int>? = null
    // Coroutine 버전
    var paramCoroutine: Pair<Int, Int>? = null
    // Flow 버전
    var paramFlow: Pair<Int, Int>? = null
    // 결과
    val items = MediatorLiveData<List<JebiItem>>()
    val winnings = map(items) { items -> items.filter { it.winning } }
    val selected = MutableLiveData<JebiItem>()

    init {
        items.addSource(getJebiMediatorUseCase.observe()) { result ->
            items.value = when (result) {
                is Result.Success -> result.data
                is Result.Error -> null
            }
        }
    }

    // MediatorUseCase 버전
    fun initMediator(winning: Int, total: Int) {
        param = Pair(winning, total)
        getJebiMediatorUseCase.execute(GetJebiParam(winning, total))
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
        else -> { // MediatorUseCase 버전
            initMediator(param!!.first + (winning ?: 0), param!!.second + (total ?: 0))
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