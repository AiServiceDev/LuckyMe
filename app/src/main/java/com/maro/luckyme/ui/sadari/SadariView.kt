package com.maro.luckyme.ui.sadari

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.maro.luckyme.R
import com.maro.luckyme.ui.sadari.data.Branch
import com.maro.luckyme.ui.sadari.data.DataHelper
import com.maro.luckyme.ui.sadari.data.Stream
import java.util.*


//
//@ 한칸에 최소 2개로 잡으니까 
// 최소값은 :  ((인원수 -1) * 2)
// 최대값은 :  ((인원수 -1) * 4)

val MIN_BRANCH_COUNT = 2
val MAX_BRANCH_COUNT = 4
val VERTICAL_COUNT = 10
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
    val TAG = SadariView::class.simpleName

    val CELL_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_cell_width)
    val CELL_HEIGHT = resources.getDimensionPixelSize(R.dimen.sadari_cell_height)

    val MIN_PLAYER_COUNT = 2
    val MAX_PLAYER_COUNT = 12
    val STEP = 10
    val KKODARI = CELL_HEIGHT * 2 // 상,하 사다리 여분

    val PLAYER_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_player_width)
    val PLAYER_WIDTH_SMALL = resources.getDimensionPixelSize(R.dimen.sadari_player_width_s)
    val PLAYER_LIST = mutableListOf<VectorDrawableCompat>().apply {
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dog, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_chicken, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_cow, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dragon, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_horse, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_monkey, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_pig, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rabbit, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rat, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_sheep, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_snake, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_tiger, null)!!)
    }
    val STROKE_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_stroke_width)


//    val PLAYER_WIDTH: Int by lazy {
//        context.resources.getDimensionPixelSize(R.dimen.sadari_player_width)
//    }


    var playerCount: Int? = null
    var bombCount: Int? = null

    var paint: Paint? = null
    var paint2: Paint? = null

    var sadari: LinkedList<Stream>? = null

    // 애니메이션 처리
    var pathMeasure: PathMeasure? = null
    var pathMatrix: Matrix? = null
    var animPath: Path = Path()
    var curIcon: VectorDrawableCompat? = null
    var curX: Float? = null
    var curY: Float? = null
    var distance: Float? = null // distance moved
    var pos: FloatArray = FloatArray(2)
    var tan: FloatArray = FloatArray(2)

    init {
        initView()
    }

    private fun initView() {
        pathMatrix = Matrix()

        paint = Paint()
        paint?.setColor(ContextCompat.getColor(context, R.color.purple_200))
        paint?.strokeWidth = STROKE_WIDTH.toFloat()

        paint2 = Paint()
        paint2?.setColor(ContextCompat.getColor(context, R.color.teal_200))
        paint2?.strokeWidth = STROKE_WIDTH.toFloat()

        setData(6, 1)

        setOnTouchListener { v, event ->
            processTouchEvent(v, event)
        }
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
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(1200, (2 * KKODARI + VERTICAL_COUNT * CELL_HEIGHT + 500).toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            sadari?.let {
                drawSadari(canvas)
                drawPlayer(canvas)
                drawHitAndMiss(canvas)
                drawPath(canvas)
            }
        }
    }

    private fun drawSadari(canvas: Canvas) {
        var sadariStartX = (PLAYER_WIDTH / 2).toFloat()
        var sadariStartY = PLAYER_WIDTH + resources.getDimensionPixelSize(R.dimen.dp8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * VERTICAL_COUNT + KKODARI

        sadari?.forEachIndexed { index, stream ->
            // 세로선
            var x = sadariStartX + CELL_WIDTH * index
            canvas?.drawLine(x, sadariStartY, x, sadariEndY, paint!!)

            // 가로선
            stream.branchList?.forEach { branch ->
                // 현재 stream의 오른쪽 branch만 그리면 모두 다 그릴 수 있다.
                // (다음 stream이 왼쪽 branch는 현재 stream의 오른쪽 branch와 같으므로...)
                if (branch.direction == DIRECTION_RIGHT) {
                    var y = sadariStartY + KKODARI + branch.position * CELL_HEIGHT
                    var startX = sadariStartX + index * CELL_WIDTH
                    var endX = startX + CELL_WIDTH
                    canvas?.drawLine(startX, y, endX, y, paint!!)
                }
            }
        }
    }

    var clicked = false

    private fun drawPlayer(canvas: Canvas) {
        for (i in 0..playerCount!! - 1) {
            var x = CELL_WIDTH * i
            PLAYER_LIST[i].apply {
                setBounds(x, 0, x + PLAYER_WIDTH, PLAYER_WIDTH)
                draw(canvas)
            }
        }
    }

    private fun drawHitAndMiss(canvas: Canvas) {
        var sadariStartY = PLAYER_WIDTH + resources.getDimensionPixelSize(R.dimen.dp8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * VERTICAL_COUNT + KKODARI
        for (i in 0..playerCount!! - 1) {
            var x = (CELL_WIDTH * i).toFloat()

            canvas.drawCircle(x+50f, sadariEndY+100f, 50f, paint2!!)
        }
    }

    private fun drawPath(canvas: Canvas) {
        if (clicked == false) {
            return
        }

        pathMatrix?.reset()

        pathMeasure?.getPosTan(distance!!, pos, tan)
        curX = pos!![0] - PLAYER_WIDTH_SMALL / 2
        curY = pos!![1] - PLAYER_WIDTH_SMALL / 2
        pathMatrix?.postTranslate(curX!!, curY!!)
        if (distance == 0f) {
            animPath.moveTo(pos[0], pos[1])
            Log.e(TAG, "pos0=${pos[0]}, pos1=${pos[1]}")
        } else {
            animPath.lineTo(pos[0], pos[1])
            Log.e(TAG, "pos0=${pos[0]}, pos1=${pos[1]}")
        }

        curIcon?.setBounds(curX?.toInt()!!, curY?.toInt()!!, curX?.toInt()!!+PLAYER_WIDTH_SMALL, curY?.toInt()!!+PLAYER_WIDTH_SMALL)
        curIcon?.draw(canvas)
        canvas.drawPath(animPath, paint2!!)

        distance = distance!! + STEP
        invalidate()
    }

    private fun processTouchEvent(v: View, event: MotionEvent): Boolean {
        if (event.action == ACTION_DOWN) {
            PLAYER_LIST.forEachIndexed { index, player ->
                if (player.bounds.left <= event.x && player.bounds.right >= event.x
                        && player.bounds.top <= event.y && player.bounds.bottom >= event.y) {
                    Log.d(TAG, "event.action=${event.action}, ${event.x}, ${event.y}")

                    clicked = true

                    pathMeasure = PathMeasure(branchToPath(DataHelper.getPlayerPathList(sadari!!, index), index), false)
                    pathMeasure?.length

                    // XXX 코드 정리
                    curIcon = player
                    distance = 0f
                    animPath?.reset()

                    invalidate()
                    return true
                }
            }
        }
        return false
    }

    private fun branchToPath(branchList: List<Branch>, playerIndex: Int): Path {
        // XXX 중복 코드
        var sadariStartX = (PLAYER_WIDTH / 2).toFloat()
        var sadariStartY = PLAYER_WIDTH + resources.getDimensionPixelSize(R.dimen.dp8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * VERTICAL_COUNT + KKODARI

        var curX = sadariStartX + CELL_WIDTH * playerIndex
        var curY = sadariStartY
        var prePosition = 0
        var path = Path().apply {
            moveTo(curX, curY)
            curY = curY + KKODARI
            lineTo(curX, curY)
            branchList.forEach {
                curY = curY + CELL_HEIGHT * (it.position - prePosition)
                lineTo(curX, curY)
                curX = curX + CELL_WIDTH * if (it.direction == DIRECTION_RIGHT) 1 else -1
                lineTo(curX, curY)
                prePosition = it.position
            }
            curY = sadariEndY
            lineTo(curX, curY)
        }

        return path
    }
}