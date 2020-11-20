package com.maro.luckyme.domain.jebi

import com.maro.luckyme.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.random.Random

class LuckyRepository {

    /**
     * @param winning 당첨 인원
     * @param total 전체 참여 인원
     */
    fun makeResult(winning: Int, total: Int): Result<Pair<List<Int>, List<Int>>> {
        val first = makeRandom(winning, total)
        val second = makeRandom(total, total)
        return Result.Success(Pair(first, second))
    }

    suspend fun makeResultCoroutine(winning: Int, total: Int): Pair<List<Int>, List<Int>> {
        return withContext(Dispatchers.IO) {
            Timber.d("[sunchulbaek] makeResultCoroutine() ${Thread.currentThread()}")
            val first = makeRandom(winning, total)
            val second = makeRandom(total, total)
            Pair(first, second)
        }
    }

    fun makeResultFlow(winning: Int, total: Int): Pair<List<Int>, List<Int>> {
        Timber.d("[sunchulbaek] makeResultFlow() ${Thread.currentThread()}")
        val first = makeRandom(winning, total)
        val second = makeRandom(total, total)
        return Pair(first, second)
    }

    private fun makeRandom(winning: Int, total: Int): List<Int> {
        val pool = mutableListOf<Int>().apply {
            for (i in 0 until total) add(i)
        }
        val ret = mutableListOf<Int>()
        val random = Random(System.currentTimeMillis())
        for (i in 0 until winning) {
            random.nextInt(pool.size).let { index ->
                ret.add(pool[index])
                pool.removeAt(index)
            }
        }
        return ret
    }
}