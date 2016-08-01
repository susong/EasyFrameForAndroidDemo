package com.dream.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/24 下午6:03
 * Description: 自定义View实现空调遥控器切换度数
 * http://blog.csdn.net/u012305710/article/details/51859542
 */
public class CustomAirMoveView extends View {

    private int circleWidth = 60;  //圆的宽度
    private Paint         mArcPaint;
    private Paint         mLinePaint;
    private Paint         mTextPaint;
    private int           mCenter;
    private int           mRadius;
    private int           insideRadius; //内部半径
    private RectF         mArcRectF;
    private SweepGradient mSweepGradient;
    //扫描度数
    private int           scanDegrees;
    private int startDegrees = 16;
    private boolean isCanMove;

    public CustomAirMoveView(Context context) {
        this(context, null);
    }

    public CustomAirMoveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomAirMoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.e("CXX", "width:" + getWidth());
        mArcPaint = new Paint();
        mArcPaint.setStrokeWidth(circleWidth);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(Color.WHITE);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(0xffdddddd);
        mLinePaint.setStrokeWidth(1);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(0xff64646f);
        mTextPaint.setTextSize(30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initSize() {
        mCenter = 720 / 2;
        mRadius = 720 / 2 - 100;
        insideRadius = mRadius - circleWidth / 2;
        mArcRectF = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);

        int[] colors = {0xFFE5BD7D, 0xFFFAAA64,
                        0xFFFFFFFF, 0xFF6AE2FD,
                        0xFF8CD0E5, 0xFFA3CBCB,
                        0xFFBDC7B3, 0xFFD1C299,
                        0xFFE5BD7D};
        float[] positions = {0, 1f / 8, 2f / 8, 3f / 8, 4f / 8, 5f / 8, 6f / 8, 7f / 8, 1};

        //渐变色
        mSweepGradient = new SweepGradient(mCenter, mCenter, colors, positions);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initSize();
        mArcPaint.setShader(null);
        //画底部纯白色圆
        canvas.drawArc(mArcRectF, 135, 270, false, mArcPaint);
        // 设置画笔渐变色
        mArcPaint.setShader(mSweepGradient);
        Log.e("CXX", "scanDegrees:" + scanDegrees);
        // 画带渐变色的圆
        canvas.drawArc(mArcRectF, 135, scanDegrees, false, mArcPaint);
        //画线 每隔3度画一条线，整个圆共画120条线
        for (int i = 0; i < 120; i++) {
            //圆心最右边内环的X坐标
            //说明：有边框的圆的半径是指：边框中心到圆心的距离，
            //如半径为100，边框宽度为20，那内环半径为90，外环半径为110.
            int insideRadius = mCenter + mRadius - circleWidth / 2;
            //圆心最右边外环的X坐标
            int outsideRadius = mCenter + mRadius + circleWidth / 2;
            // 去除底部不包含的区域 只旋转不划线
            if (i <= 15 || i >= 45) {
                if (i % 15 == 0) {
                    //整点时X轴向外延伸
                    outsideRadius += 25;
                }
                canvas.drawLine(insideRadius, mCenter, outsideRadius, mCenter, mLinePaint);
            }
            //旋转
            canvas.rotate(3, mCenter, mCenter);
        }
        //画文字+50代表这个字距离圆外边的距离
        //斜边，即文字距离圆心的长度。
        int c = mRadius + circleWidth / 2 + 50;
        //x代表文字的中心距离圆心的距离 这是原点中心正左边字的长度
        //勾股定理
        int x = (int) Math.sqrt((c * c / 2));
        Log.e("CXX", "x:" + x + " mcenter:" + mCenter);
        canvas.drawText(startDegrees + "", mCenter - x, mCenter + x, mTextPaint);
        canvas.drawText(startDegrees + 2 + "", mCenter - c, mCenter, mTextPaint);
        canvas.drawText(startDegrees + 4 + "", mCenter - x, mCenter - x, mTextPaint);
        canvas.drawText(startDegrees + 6 + "", mCenter, mCenter - c, mTextPaint);
        canvas.drawText(startDegrees + 8 + "", mCenter + x, mCenter - x, mTextPaint);
        canvas.drawText(startDegrees + 10 + "", mCenter + c, mCenter, mTextPaint);
        canvas.drawText(startDegrees + 12 + "", mCenter + x, mCenter + x, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCanMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //判断手指在空白区域不能滑动
                if (!isCanMove) {
                    return false;
                }
                float x = event.getX();
                float y = event.getY();
                float firstX = event.getX();
                float firstY = event.getY();
                //判断当前手指距离圆心的距离 代表在圆心的右侧
                if (x > mCenter) {
                    x = x - mCenter;
                } else {
                    x = mCenter - x;
                }
                if (y < mCenter) {
                    y = mCenter - y;
                } else {
                    y = y - mCenter;
                }
                Log.e("CXX", "sqrt : " + Math.sqrt(x * x + y * y) + " radius : " + (mRadius - 40));
                //判断当前手指是否在空白区域
                if (Math.sqrt(x * x + y * y) < (mRadius - 40)) {
                    Log.e("CXX", "终止滑动");
                    isCanMove = false;
                    return false;
                }
                //邻边比斜边
                float v = x / (float) Math.sqrt(x * x + y * y);
                //根据cos求角度
                double acos = Math.acos(v);
                acos = Math.toDegrees(acos);
                Log.e("CXX", "acos" + acos);
                if (firstX >= mCenter && firstY <= mCenter) {
                    //第一象限
                } else if (firstX <= mCenter && firstY <= mCenter) {
                    //第二象限
                } else if (firstX <= mCenter && firstY >= mCenter) {
                    //第三象限
                } else if (firstX >= mCenter && firstY >= mCenter) {
                    //第四象限
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
