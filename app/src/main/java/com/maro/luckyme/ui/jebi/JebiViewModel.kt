package com.maro.luckyme.ui.jebi

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.maro.luckyme.R
import com.maro.luckyme.domain.LuckyRepository
import com.maro.luckyme.domain.Result
import timber.log.Timber

class JebiViewModel : ViewModel() {

    private val icons = listOf(
        R.drawable.ic_rat,
        R.drawable.ic_cow,
        R.drawable.ic_tiger,
        R.drawable.ic_rabbit,
        R.drawable.ic_dragon,
        R.drawable.ic_snake,
        R.drawable.ic_horse,
        R.drawable.ic_sheep,
        R.drawable.ic_monkey,
        R.drawable.ic_chicken,
        R.drawable.ic_dog,
        R.drawable.ic_pig
    )

    val param = MutableLiveData<Pair<Int, Int>>()
    val result = map(param) {
        val repository = LuckyRepository()
        repository.makeResult(it.first, it.second)
    }
    val items = MediatorLiveData<List<JebiItem>>()
    val selected = MutableLiveData<JebiItem>()

    init {
        items.addSource(result) {
            items.value = when (it) {
                is Result.Success -> mutableListOf<JebiItem>().apply {
                    Timber.d("[sunchulbaek] 당첨 = ${it.data.first}")
                    Timber.d("[sunchulbaek] 전체 = ${it.data.second}")
                    it.data.second.forEachIndexed { index, shuffledIndex ->
                        add(JebiItem(icons[shuffledIndex], it.data.first.contains(index)))
                    }
                }
                is Result.Error -> TODO()
            }
        }
    }

    fun minus() {
        param.value = Pair(2, (param.value?.second ?: 0) - 1)
    }

    fun plus() {
        param.value = Pair(2, (param.value?.second ?: 0) + 1)
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