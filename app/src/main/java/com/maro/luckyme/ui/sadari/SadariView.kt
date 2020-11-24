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
import com.maro.luckyme.ui.sadari.data.DataHelper
import com.maro.luckyme.ui.sadari.data.Stream
import java.util.*


//
//@ 한칸에 최소 2개로 ø잡으니까 
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

    var MARGIN = resources.getDimensionPixelSize(R.dimen.dp16)

    val DEFAULT_PLAYER_COUNT = 4
    val DEFAULT_BOMB_COUNT = 1

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

    val PLAYER_HIT_LIST = mutableListOf<VectorDrawableCompat>().apply {
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


    var playerCount: Int = DEFAULT_PLAYER_COUNT
    var bombCount: Int = DEFAULT_BOMB_COUNT

    lateinit var paint: Paint
    lateinit var animPaint: Paint
    lateinit var hitPaint: Paint

    lateinit var sadari: LinkedList<Stream>
    lateinit var bombIndexList: List<Int>

    // 애니메이션 처리
    var pathMeasure: PathMeasure? = null
    var _matrix: Matrix? = null
    var animPath: Path = Path()
    var curIcon: VectorDrawableCompat? = null
    var curX: Float = 0f
    var curY: Float = 0f
    var distance: Float? = null // distance moved
    var pos: FloatArray = FloatArray(2)
    var tan: FloatArray = FloatArray(2)

    init {
        initAttr(attrs, defStyleAttr)
        initView()

        play()
    }

    private fun initAttr(attrs: AttributeSet?, defStyleAttr:Int) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SadariView, defStyleAttr, 0).apply {
            try {
                playerCount = getInt(R.styleable.SadariView_playerCount, DEFAULT_PLAYER_COUNT)
                bombCount = getInt(R.styleable.SadariView_bombCount, DEFAULT_BOMB_COUNT)
            }
            finally {
                recycle()
            }
        }
    }

    private fun initView() {
        _matrix = Matrix()

        paint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.indigo_200)
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

        setOnTouchListener { v, event ->
            processTouchEvent(v, event)
        }
    }

    fun play() {
        sadari = DataHelper.makeSadariData(playerCount)
        bombIndexList = DataHelper.makeBombIndexList(playerCount, bombCount)
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

        var sadariStartX = (PLAYER_WIDTH / 2).toFloat() + MARGIN
        var sadariStartY = PLAYER_WIDTH + resources.getDimensionPixelSize(R.dimen.dp8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * VERTICAL_COUNT + KKODARI

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

    var clicked = false

    private fun drawPlayer(canvas: Canvas) {
        for (i in 0..playerCount - 1) {
            var x = CELL_WIDTH * i + MARGIN
            PLAYER_LIST[i].apply {
                setBounds(x, 0, x + PLAYER_WIDTH, PLAYER_WIDTH)
                draw(canvas)
            }
        }
    }

    private fun drawHitAndMiss(canvas: Canvas) {
        var sadariStartY = PLAYER_WIDTH + resources.getDimensionPixelSize(R.dimen.dp8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * VERTICAL_COUNT + KKODARI

        for (i in 0..playerCount - 1) {
            var x = (CELL_WIDTH * i).toFloat() + MARGIN

            canvas.drawCircle(x+50f, sadariEndY+50f+30f, 50f, paint)
            if (bombIndexList.contains(i)) {
                canvas.drawText("꽝", x+32, sadariEndY+50f+35, hitPaint)
            } else {
                canvas.drawText("통과", x+20, sadariEndY+50f+35, hitPaint)
            }

        }

//        for (i in 0..playerCount - 1) {
//            var x = CELL_WIDTH * i + MARGIN
//            PLAYER_HIT_LIST[i].apply {
//                setBounds(x, sadariEndY.toInt(), x + PLAYER_WIDTH_SMALL, sadariEndY.toInt() + PLAYER_WIDTH_SMALL)
//                draw(canvas)
//            }
//        }
    }

    private fun drawPath(canvas: Canvas) {
        if (clicked == false) {
            return
        }

        _matrix?.reset()

        pathMeasure?.getPosTan(distance!!, pos, tan)
        curX = pos[0] - PLAYER_WIDTH_SMALL / 2 + MARGIN
        curY = pos[1] - PLAYER_WIDTH_SMALL / 2
        _matrix?.postTranslate(curX!!, curY!!)
        if (distance == 0f) {
            animPath.moveTo(pos[0] + MARGIN, pos[1])
        } else {
            animPath.lineTo(pos[0] + MARGIN, pos[1])
        }
        Log.e("XXX", "===> animPath=${animPath.isEmpty}")

        canvas.drawPath(animPath, animPaint)
        curIcon?.setBounds(curX.toInt(), curY.toInt(), curX.toInt()+PLAYER_WIDTH_SMALL, curY.toInt()+PLAYER_WIDTH_SMALL)
        curIcon?.draw(canvas)


        distance = distance!! + STEP
        invalidate()
    }

    private fun processTouchEvent(v: View, event: MotionEvent): Boolean {
        if (event.action == ACTION_UP) {
            PLAYER_LIST.forEachIndexed { index, player ->
                if (player.bounds.left <= event.x && player.bounds.right >= event.x
                        && player.bounds.top <= event.y && player.bounds.bottom >= event.y) {
                    Log.d(TAG, "event.action=${event.action}, ${event.x}, ${event.y}")

                    clicked = true

                    pathMeasure = PathMeasure(branchToPath(DataHelper.getPlayerPathList(sadari, index), index), false)
                    pathMeasure?.length

                    // XXX 코드 정리
                    curIcon = player
                    distance = 0f
                    animPath?.reset()

                    invalidate()
                }
            }
        }
        return true
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