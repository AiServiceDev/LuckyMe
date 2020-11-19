package com.maro.luckyme

import com.maro.luckyme.ui.sadari.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val TAG = ExampleUnitTest::class.simpleName
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sadari() {
        val playerCount = 4

        var sadari = makeSadariData(playerCount)

        sadari.forEach {
            println("Stream=${it.branchList}")
        }
    }

    private fun makeSadariData(playerCount: Int): LinkedList<Stream> {
        var sadari = LinkedList<Stream>()

        var currentIndex: Int? = null

        for (i in 1..playerCount-1) {
            currentIndex = makeOneSadari(sadari, currentIndex)
        }

        sort(sadari)

        return sadari
    }

    private fun makeOneSadari(sadari: LinkedList<Stream>, currentIndex: Int?): Int {
        var _currentIndex: Int? = currentIndex
        var branchCount = DataHelper.makeBranchCount()
        if (_currentIndex == null) {
            _currentIndex = 0
            sadari.addLast(Stream())
        }

        var leftStream = sadari.get(_currentIndex)
        var rightStream = Stream()

        while (true) {
            var position = Random().nextInt(VERTICAL_COUNT) + 1 // position은 1부터 VERTICAL_COUNT 까지..
            if (checkDuplication(position, leftStream.branchList)) {
                continue
            }

            leftStream.branchList?.add(Branch(position, DIRECTION_RIGHT))
            rightStream.branchList?.add(Branch(position, DIRECTION_LEFT))

            if (rightStream.branchList?.size == branchCount) {
                break
            }
        }

        sadari.addLast(rightStream)

        return _currentIndex + 1
    }

    private fun sort(sadari: LinkedList<Stream>) {
        sadari.forEach {
            it.branchList?.sortBy {
                it.position
            }
        }
    }


    private fun checkDuplication(position: Int, branchList: List<Branch>?): Boolean {
        branchList?.forEach {
            if (it.position == position) {
                return true
            }
        }

        return false
    }
}