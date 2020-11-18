package com.maro.luckyme.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val sadariEvent = MutableLiveData<Boolean>()
    val diceEvent = MutableLiveData<Boolean>()
    val jebiEvent = MutableLiveData<Boolean>()

    fun onSadariMenuClicked() {
        sadariEvent.value = true
    }

    fun onDiceMenuClicked() {
        diceEvent.value = true
    }

    fun onJebiMenuClicked() {
        jebiEvent.value = true
    }
}