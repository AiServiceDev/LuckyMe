package com.maro.luckyme.ui.jebi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maro.luckyme.R
import kotlin.random.Random

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

    val items = MutableLiveData<List<JebiItem>>()
    val selected = MutableLiveData<JebiItem>()

    fun minus() = items.value?.let {
        items.value = it.minus(it[it.size - 1])
    }

    fun plus() {
        items.value = items.value?.plus(JebiItem(icons[items.value!!.size]))
    }

    fun select(index: Int) {
        items.value = items.value?.mapIndexed { index2, item ->
            if (index == index2) {
                JebiItem(item.icon, true).apply {
                    this@JebiViewModel.selected.value = this
                }
            } else item
        }
    }

    fun confirm() {
        selected.value = selected.value?.apply {
            result = if (Random(System.currentTimeMillis()).nextBoolean()) "통과" else "꽝"
        }
    }

    fun close() {
        selected.value = null
    }
}