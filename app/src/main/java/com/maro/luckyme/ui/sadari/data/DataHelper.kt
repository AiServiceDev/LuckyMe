package com.maro.luckyme.ui.sadari.data

import com.maro.luckyme.ui.sadari.data.Constants.DIRECTION_LEFT
import com.maro.luckyme.ui.sadari.data.Constants.DIRECTION_RIGHT
import com.maro.luckyme.ui.sadari.data.Constants.MAX_BRANCH_COUNT
import com.maro.luckyme.ui.sadari.data.Constants.MIN_BRANCH_COUNT
import com.maro.luckyme.ui.sadari.data.Constants.TOTAL_BRANCH_COUNT
import java.util.*

object DataHelper {
    private fun makeBranchCount(): Int {
        return Random().nextInt(MAX_BRANCH_COUNT - MIN_BRANCH_COUNT + 1) + MIN_BRANCH_COUNT
    }

    fun makeSadariData(playerCount: Int): LinkedList<Stream> {
        var sadari = LinkedList<Stream>()

        var currentIndex: Int? = null

        for (i in 1..playerCount-1) {
            currentIndex = makeOneSadari(sadari, currentIndex)
        }

        sort(sadari)

        return sadari
    }

    fun makeBombIndexList(playerCount: Int, bombCount: Int): List<Int> {
        var bombIndexList = arrayListOf<Int>()
        for (i in 0..playerCount-1) {
            var index = Random().nextInt(playerCount)
            if (!bombIndexList.contains(index)) {
                bombIndexList.add(index)

                if (bombIndexList.size == bombCount) {
                    break
                }
            }
        }

        return bombIndexList
    }

    fun getPlayerPathList(sadari: LinkedList<Stream>, playerIndex: Int): List<Branch> {
        var pathList = mutableListOf<Branch>()

        var curPlayerIndex = playerIndex
        var curPosition = -1
        var hasNext = false
        while (true) {
            hasNext = false
            run breaker@ {
                sadari[curPlayerIndex].branchList?.forEach { branch ->
                    if (branch.position > curPosition) {
                        curPosition = branch.position
                        curPlayerIndex +=
                                if (branch.direction == DIRECTION_RIGHT) {
                                    1
                                } else {
                                    -1
                                }
                        pathList.add(branch)
                        hasNext = true
                        return@breaker
                    }
                }
            }

            if (!hasNext) {
                break
            }
        }

        return pathList
    }

    private fun makeOneSadari(sadari: LinkedList<Stream>, currentIndex: Int?): Int {
        var _currentIndex: Int? = currentIndex
        var branchCount = makeBranchCount()
        if (_currentIndex == null) {
            _currentIndex = 0
            sadari.addLast(Stream())
        }

        var leftStream = sadari.get(_currentIndex)
        var rightStream = Stream()

        while (true) {
            // position 0부터 시작
            var position = Random().nextInt(TOTAL_BRANCH_COUNT)
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