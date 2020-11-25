package com.maro.luckyme.ui.sadari

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.maro.luckyme.R
import com.maro.luckyme.ui.sadari.data.Branch
import com.maro.luckyme.ui.sadari.data.Constants
import com.maro.luckyme.ui.sadari.data.Constants.DEFAULT_BOMB_COUNT
import com.maro.luckyme.ui.sadari.data.Constants.DEFAULT_PLAYER_COUNT
import com.maro.luckyme.ui.sadari.data.Constants.DIRECTION_RIGHT
import com.maro.luckyme.ui.sadari.data.Constants.SPEED
import com.maro.luckyme.ui.sadari.data.Constants.STATUS_STARTED
import com.maro.luckyme.ui.sadari.data.Constants.STATUS_WAITING
import com.maro.luckyme.ui.sadari.data.Constants.TOTAL_BRANCH_COUNT
import com.maro.luckyme.ui.sadari.data.DataHelper
import com.maro.luckyme.ui.sadari.data.Stream
import java.util.*


/**
 *  KKODARI는 사다리 최상단, 최하단 부분을 뜻함
 *
 * |  |  | <= KKODARI
 * |--|  |
 * |  |--|
 * |--|  |
 * |  |  | <= KKODARI
 */

class SadariView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    val TAG = SadariView::class.simpleName

    val DP16 = resources.getDimensionPixelSize(R.dimen.dp16)
    val DP8 = resources.getDimensionPixelSize(R.dimen.dp8)
    val DP20 = resources.getDimensionPixelSize(R.dimen.dp20)
    val DP24 = resources.getDimensionPixelSize(R.dimen.dp24)

    val CELL_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_cell_width)
    val CELL_HEIGHT = resources.getDimensionPixelSize(R.dimen.sadari_cell_height)

    val KKODARI = CELL_HEIGHT * 2 // 상,하 사다리 여분

    val PLAYER_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_player_width)
    val PLAYER_WIDTH_SMALL = resources.getDimensionPixelSize(R.dimen.sadari_player_width_s)
    val PLAYER_LIST = mutableListOf<VectorDrawableCompat>().apply {
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rat, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_cow, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_tiger, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rabbit, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dragon, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_snake, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_horse, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_sheep, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_monkey, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_chicken, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dog, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_pig, null)!!)
    }

    val PLAYER_HIT_LIST = mutableListOf<VectorDrawableCompat>().apply {
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rat, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_cow, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_tiger, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rabbit, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dragon, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_snake, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_horse, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_sheep, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_monkey, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_chicken, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dog, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_pig, null)!!)
    }

    val COLOR_LIST = mutableListOf<Int>().apply {
        add(R.color.rat)
        add(R.color.cow)
        add(R.color.tiger)
        add(R.color.rabbit)
        add(R.color.dragon)
        add(R.color.snake)
        add(R.color.horse)
        add(R.color.sheep)
        add(R.color.monkey)
        add(R.color.chicken)
        add(R.color.dog)
        add(R.color.pig)
    }

    val STROKE_WIDTH = resources.getDimensionPixelSize(Constants.STROKE_WIDTH)

    var playerCount: Int = DEFAULT_PLAYER_COUNT
    var bombCount: Int = DEFAULT_BOMB_COUNT

    var viewStartX = 0 // view의 시작 위치

    lateinit var paint: Paint
    lateinit var paint2: Paint
    lateinit var animPaint: Paint
    lateinit var hitPaint: Paint
    lateinit var startPaint: Paint


    lateinit var sadari: LinkedList<Stream>
    lateinit var bombIndexList: List<Int>
    var playerResultMap: MutableMap<Int, PlayerResult> = mutableMapOf()

    // 애니메이션 처리
    var _matrix: Matrix = Matrix()
    var animPath: Path = Path()
    var curX: Float = 0f
    var curY: Float = 0f
    var pos: FloatArray = FloatArray(2)
    var tan: FloatArray = FloatArray(2)

    // 게임 상태
    var playStatus = STATUS_WAITING

    init {
        initView()
    }

    private fun initView() {
        paint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.indigo_200)
            strokeWidth = STROKE_WIDTH.toFloat()
            style = Paint.Style.FILL
        }
        paint2 = Paint().apply {
            color = ContextCompat.getColor(context, R.color.teal_700)
            strokeWidth = STROKE_WIDTH.toFloat()
            style = Paint.Style.FILL
        }

        animPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.teal_200)
            strokeWidth = STROKE_WIDTH.toFloat()
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.BEVEL
            strokeCap = Paint.Cap.SQUARE
        }

        hitPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.white)
            style = Paint.Style.FILL
            textSize = resources.getDimensionPixelSize(R.dimen.dp12).toFloat()
        }

        startPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.white)
            style = Paint.Style.FILL
            textSize = resources.getDimensionPixelSize(R.dimen.dp24).toFloat()
        }

        setOnTouchListener { v, event ->
            processTouchEvent(v, event)
        }
    }

    fun setData(playerCount: Int, bombCount: Int) {
        this@SadariView.playerCount = playerCount
        this@SadariView.bombCount = bombCount

        play()
    }

    fun play() {
        sadari = DataHelper.makeSadariData(playerCount)
        bombIndexList = DataHelper.makeBombIndexList(playerCount, bombCount)
        playerResultMap.clear()
        playStatus = STATUS_WAITING

        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var point = Point()
        display.getSize(point)

        // Width
        var width = CELL_WIDTH * playerCount + DP16 + DP16
        if (point.x > width) {
            viewStartX = (point.x - width) / 2 + DP16
            width = point.x
        } else {
            viewStartX = DP16
        }

        // Height
        var height = PLAYER_WIDTH + DP8 + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI + DP8 + DP20 * 2 + DP8

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            sadari?.let {
                drawSadari(canvas)
                drawPlayer(canvas)
                drawHitAndMiss(canvas)
                drawAnimPath(canvas)
                drawStartButton(canvas)
            }
        }
    }

    private fun drawSadari(canvas: Canvas) {
        var sadariStartX = viewStartX + (PLAYER_WIDTH / 2).toFloat()
        var sadariStartY = (PLAYER_WIDTH + DP8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI

        sadari?.forEachIndexed { index, stream ->
            // 세로선
            var x = sadariStartX + CELL_WIDTH * index
            canvas?.drawLine(x, sadariStartY, x, sadariEndY, paint)

            // 가로선
            stream.branchList?.forEach { branch ->
                // 현재 stream의 오른쪽 branch만 그리면 모두 다 그릴 수 있다.
                // (다음 stream이 왼쪽 branch는 현재 stream의 오른쪽 branch와 같으므로...)
                if (branch.direction == DIRECTION_RIGHT) {
                    var y = sadariStartY + KKODARI + branch.position * CELL_HEIGHT
                    var startX = sadariStartX + index * CELL_WIDTH
                    var endX = startX + CELL_WIDTH
                    canvas?.drawLine(startX, y, endX, y, paint)
                }
            }
        }
    }

    private fun drawPlayer(canvas: Canvas) {
        for (i in 0..playerCount - 1) {
            var x = viewStartX + CELL_WIDTH * i
            PLAYER_LIST[i].apply {
                setBounds(x, 0, x + PLAYER_WIDTH, PLAYER_WIDTH)
                draw(canvas)
            }
        }
    }

    private fun drawHitAndMiss(canvas: Canvas) {
        var sadariStartY = PLAYER_WIDTH + DP8
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI

        for (i in 0..playerCount - 1) {
            var x = viewStartX + (CELL_WIDTH * i).toFloat()

            canvas.drawCircle(x + DP20.toFloat(), sadariEndY + DP20.toFloat() + DP8, DP20.toFloat(), paint)
            // XXX 문구
            if (bombIndexList.contains(i)) {
                canvas.drawText("꽝", x + 34, sadariEndY + DP20.toFloat() + 34, hitPaint)
            } else {
                canvas.drawText("통과", x + 22, sadariEndY + DP20.toFloat() + 34, hitPaint)
            }

        }
    }

    private fun drawAnimPath(canvas: Canvas) {
        if (playerResultMap.isEmpty()) {
            return
        }

        _matrix.reset()

        for ((index, playerResult) in playerResultMap) {
            playerResult.pathMeasure?.getPosTan(playerResult.distance!!, pos, tan)
            curX = viewStartX + pos[0] - PLAYER_WIDTH_SMALL / 2
            curY = pos[1] - PLAYER_WIDTH_SMALL / 2
            _matrix.postTranslate(curX!!, curY!!)
            if (playerResult.distance == 0f) {
                playerResult.animPath.moveTo(viewStartX + pos[0], pos[1])
            } else {
                playerResult.animPath.lineTo(viewStartX + pos[0], pos[1])
            }
            Log.e("XXX", "===> animPath=${animPath.isEmpty}")

            animPaint.color = ContextCompat.getColor(context, COLOR_LIST[index])
            canvas.drawPath(playerResult.animPath, animPaint)
            playerResult.icon?.apply {
                setBounds(curX.toInt(), curY.toInt(), curX.toInt() + PLAYER_WIDTH_SMALL, curY.toInt() + PLAYER_WIDTH_SMALL)
                draw(canvas)
            }

            playerResult.distance = playerResult.distance + SPEED
        }

        invalidate()
    }

    private fun drawStartButton(canvas: Canvas) {
        if (playStatus != STATUS_WAITING) {
            return
        }

        var rect = getSadariRect()
        var left = rect.left.toFloat()
        var right = rect.right.toFloat()
        var top = rect.top.toFloat()
        var bottom = rect.bottom.toFloat()

        var margin = DP24

        // DP24는 텍스트 사이즈
        var textX = left + (right - left) / 2 - (DP24 * 4 / 2) + 10 // 10을 더한 이유는 억지로 위치를 맞추기 위함
        var textY = top + (bottom - top) / 2

        var buttonRect = getStartButtonRect()

        canvas.drawRect(left - margin, top + margin, right + margin, bottom - margin, paint)
        canvas.drawRect(buttonRect.left.toFloat(), buttonRect.top.toFloat(), buttonRect.right.toFloat(), buttonRect.bottom.toFloat(), paint2)
        canvas.drawText("시작하기", textX, textY, startPaint)
    }

    private fun processTouchEvent(v: View, event: MotionEvent): Boolean {
        if (event.action == ACTION_UP) {
            when (playStatus) {
                STATUS_WAITING -> {
                    var buttonRect = getStartButtonRect()
                    if (buttonRect.left <= event.x && buttonRect.right >= event.x
                            && buttonRect.top <= event.y && buttonRect.bottom >= event.y) {
                        playStatus = STATUS_STARTED
                        invalidate()
                    }
                }
                STATUS_STARTED -> {
                    PLAYER_LIST.forEachIndexed { index, player ->
                        if (player.bounds.left <= event.x && player.bounds.right >= event.x
                                && player.bounds.top <= event.y && player.bounds.bottom >= event.y) {

                            if (!playerResultMap.containsKey(index)) {
                                playerResultMap.put(index, PlayerResult(
                                        PathMeasure(branchToPath(DataHelper.getPlayerPathList(sadari, index), index), false),
                                        PLAYER_HIT_LIST[index],
                                ))
                            }

                            invalidate()
                        }
                    }
                }
            }
        }
        return true
    }

    private fun branchToPath(branchList: List<Branch>, playerIndex: Int): Path {
        // XXX 중복 코드
        var sadariStartX = (PLAYER_WIDTH / 2).toFloat()
        var sadariStartY = (PLAYER_WIDTH + DP8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI

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

    private fun getSadariRect(): Rect {
        return Rect().apply {
            top = PLAYER_WIDTH + DP8
            bottom = top + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI
            left = viewStartX + PLAYER_WIDTH / 2
            right = left + CELL_WIDTH * (playerCount - 1)
        }
    }

    private fun getStartButtonRect(): Rect {
        var sadariRect = getSadariRect()
        return Rect().apply {
            top = sadariRect.top + DP24 * 4
            bottom = sadariRect.bottom - DP24 * 4
            left = sadariRect.left + DP24 * 2
            right = sadariRect.right - DP24 * 2
        }
    }
}

data class PlayerResult(
    var pathMeasure: PathMeasure,
    var icon: VectorDrawableCompat,
    var distance: Float = 0f,
    var animPath: Path = Path()
)