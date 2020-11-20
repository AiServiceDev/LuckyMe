package com.maro.luckyme.data.common

import com.maro.luckyme.R


/**
 * Created by smartman99@ncsoft.com on 2020/11/20.
 *
 */
class CommonData {
    companion object {
        val icon12KanjiList = listOf(
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

        @JvmStatic
        fun get12KanjiListByIndex(index: Int): Int? {
            if (index in icon12KanjiList.indices) {
                return icon12KanjiList[index]
            }
            return null
        }
    }

}