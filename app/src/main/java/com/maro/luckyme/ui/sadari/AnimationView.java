package com.maro.luckyme.ui.sadari;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AnimationView extends View {

    Paint paint;
    Paint paint2;

    Bitmap bm;
    int bm_offsetX, bm_offsetY;

    Path animPath;
    Path animPath2;
    PathMeasure pathMeasure;
    float pathLength;

    float step;   //distance each step
    float distance;  //distance moved
    float curX, curY;

    float[] pos;
    float[] tan;

    Matrix matrix;

    Path touchPath;

    public AnimationView(Context context) {
        super(context);
        initMyView();
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMyView();
    }

    public void initMyView() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
        paint2.setStrokeWidth(6);
        paint2.setStyle(Paint.Style.STROKE);

        bm = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_up_float);
        bm_offsetX = bm.getWidth() / 2;
        bm_offsetY = bm.getHeight() / 2;

        animPath = new Path();
        animPath2 = new Path();

        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();

        touchPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (animPath.isEmpty()) {
            return;
        }

        canvas.drawPath(animPath, paint);
        matrix.reset();

        pathMeasure.getPosTan(distance, pos, tan);
        curX = pos[0] - bm_offsetX;
        curY = pos[1] - bm_offsetY;
        matrix.postTranslate(curX, curY);
        if (distance == 0) {
            animPath2.moveTo(pos[0], pos[1]);
        } else {
            animPath2.lineTo(pos[0], pos[1]);
            Log.e("XXX", "pos0="+ pos[0] +", pos1="+pos[1]);
        }

        canvas.drawPath(animPath2, paint2);
        canvas.drawBitmap(bm, matrix, null);

        distance += step;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchPath.reset();
                touchPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                touchPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                touchPath.lineTo(event.getX(), event.getY());
                animPath = new Path(touchPath);
                animPath2.reset();

                pathMeasure = new PathMeasure(animPath, false);
                pathLength = pathMeasure.getLength();

                Log.e("XXX", "pathLength="+ pathLength);

                step = 5;
                distance = 0;
                curX = 0;
                curY = 0;


                invalidate();

                break;

        }

        return true;
    }


}