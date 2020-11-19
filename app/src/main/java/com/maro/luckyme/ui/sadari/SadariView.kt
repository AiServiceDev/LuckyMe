package com.maro.luckyme.ui.sadari

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*


//
//@ 한칸에 최소 2개로 잡으니까 
// 최소값은 :  ((인원수 -1) * 2)
// 최대값은 :  ((인원수 -1) * 4)

val MIN_BRANCH_COUNT = 2
val MAX_BRANCH_COUNT = 4
val VERTICAL_COUNT = 20
val DIRECTION_LEFT = 0
val DIRECTION_RIGHT = 1

/**
 *
 * |  |  | <= KKODARI
 * |--|  |
 * |  |--|
 * |--|  |
 * |  |  | <= KKODARI
 */

class SadariView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    val MIN_VERTICAL_GAP = 50f

    val MIN_PLAYER_COUNT = 2
    val MAX_PLAYER_COUNT = 12
    val KKODARI = 100f // 상,하 사다리 여분

    // 사다리 시작 위치
    var START_X = 100f
    var START_Y = 100f
    var END_Y = START_Y * VERTICAL_COUNT


    var playerCount: Int? = null
    var bombCount: Int? = null

    var paint: Paint? = null

    var distance = 100f
    var sadari: LinkedList<Stream>? = null

    init {
        paint = Paint()
        paint?.setColor(Color.RED)
        paint?.strokeWidth = 10f

        setData(6, 1)
    }

    fun setData(playerCount: Int, bombCount: Int) {
        this@SadariView.playerCount = playerCount
        this@SadariView.bombCount = bombCount

//        var branchCount = DataHelper.makeBranchCount(playerCount)

//        mutableListOf<Stream>().apply {
//            for (i in 1..playerCount) {
//                add(
//                    Stream()
//                )
//            }
//        }

        sadari = DataHelper.makeSadariData(playerCount)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(1200, (2 * KKODARI + VERTICAL_COUNT * MIN_VERTICAL_GAP).toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        sadari?.let {
            drawSadari(canvas)

            drawPlayer(canvas)
        }
    }

    private fun drawSadari(canvas: Canvas?) {


        sadari?.let { sadari ->
            // 세로
            for (i in 1..sadari?.size) {
                var x = START_X + distance * i
                canvas?.drawLine(x, START_Y-KKODARI, x, END_Y+KKODARI, paint!!)
            }
        }


        sadari?.forEachIndexed { index, stream ->
            // 세로
            var x = START_X + distance * index
            canvas?.drawLine(x, START_Y-KKODARI, x, END_Y+KKODARI, paint!!)

            // 가로
            stream.branchList?.forEach { branch ->
                if (branch.direction == DIRECTION_RIGHT) {
                    var y = START_Y + branch.position * MIN_VERTICAL_GAP
                    var startX = START_X + index * distance
                    var endX = startX + distance
                    canvas?.drawLine(startX, y, endX, y, paint!!)
                }
            }
        }
    }

    private fun drawPlayer(canvas: Canvas?) {
//        canvas.drawBitmap()
    }

}

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
            var position = Random().nextInt(VERTICAL_COUNT)
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

data class Stream(
    val branchList: MutableList<Branch>? = mutableListOf()
)

data class Branch(
    val position: Int,
    val direction: Int // 0 왼쪽, 1 오른쪽
)