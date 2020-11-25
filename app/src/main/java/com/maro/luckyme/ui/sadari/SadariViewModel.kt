package com.maro.luckyme.ui.sadari

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maro.luckyme.ui.sadari.data.Constants

class SadariViewModel : ViewModel() {

    private val _playerCount = MutableLiveData<Int>()
    val playerCount: LiveData<Int>
        get() = _playerCount

    private val _bombCount = MediatorLiveData<Int>()
    val bombCount: LiveData<Int>
        get() = _bombCount

    private val _maxBombCount = MediatorLiveData<Int>()
    val maxBombCount: LiveData<Int>
        get() = _maxBombCount

    init {
        _maxBombCount.addSource(_playerCount) {
            Log.e("XXX", "==> ${it}")
            _maxBombCount.value = it / 2
        }

        _bombCount.addSource(_maxBombCount) {
            _bombCount.value?.let { bombCount ->
                _maxBombCount.value?.let { maxBombCount ->
                    if (bombCount > maxBombCount) {
                        _bombCount.value = _maxBombCount.value
                    }
                }
            }
        }

        // XXX 저장된 값을 가져와야 함
        _playerCount.value = Constants.DEFAULT_PLAYER_COUNT
        _bombCount.value = Constants.DEFAULT_BOMB_COUNT

    }

    fun onMinusPlayerClicked() {
        if (_playerCount.value == Constants.MIN_PLAYER_COUNT) {
            return
        }

        _playerCount.value = _playerCount.value!! - 1
    }

    fun onPlusPlayerClicked() {
        if (_playerCount.value == Constants.MAX_PLAYER_COUNT) {
            return
        }

        _playerCount.value = _playerCount.value!! + 1
    }

    fun onMinusBombClicked() {
        if (_bombCount.value == Constants.MIN_BOMB_COUNT) {
            return
        }

        _bombCount.value = _bombCount.value!! - 1
    }

    fun onPlusBombClicked() {
        if (_bombCount.value == _playerCount.value!! / 2) {
            return
        }

        _bombCount.value = _bombCount.value!! + 1
    }
}