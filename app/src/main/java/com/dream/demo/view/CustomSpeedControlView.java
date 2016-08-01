package com.dream.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/21 下午4:37
 * Description: 汽车速度仪表盘
 * http://blog.csdn.net/lxk_1993/article/details/51373269
 */
public class CustomSpeedControlView extends View {

    private Context mContext;
    //屏幕宽高
    private int     mScreenWidth, mScreenHeight;
    //屏幕密度
    private float mDensity;
    //仪表盘圆的圆心
    private int   mPointX, mPointY;
    //仪表盘圆的半径
    private float mRadius;

    public CustomSpeedControlView(Context context) {
        this(context, null);
    }

    public CustomSpeedControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSpeedControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mDensity = displayMetrics.density;
        //初始化圆心和半径
        mRadius = mScreenWidth / 3;
        mPointX = mPointY = mScreenWidth / 2;
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背影色
        canvas.drawColor(Color.BLACK);
        //绘制圆
        drawCircle(canvas);
        //绘制刻度
        drawScale(canvas);
        //绘制速度标识文字
        drawText(canvas);
    }

    /**
     * 绘制圆
     */
    private void drawCircle(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);

        //绘制一个实心的圆做仪表盘背景
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF343434);
        paint.setStrokeWidth(3 * mDensity);
        canvas.drawCircle(mPointX, mPointY, mRadius, paint);

        //外圈2个圆
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xBF3F6AB5);
        paint.setStrokeWidth(3 * mDensity);
        canvas.drawCircle(mPointX, mPointY, mRadius, paint);
        paint.setStrokeWidth(2 * mDensity);
        canvas.drawCircle(mPointX, mPointY, mRadius - 5 * mDensity, paint);

        //内圈2个圆
        paint.setStrokeWidth(2 * mDensity);
        paint.setColor(0xE73F51B5);
        canvas.drawCircle(mPointX, mPointY, mRadius / 2, paint);
        paint.setColor(0x7E3F51B5);
        canvas.drawCircle(mPointX, mPointY, mRadius / 2 + 2 * mDensity, paint);
    }

    /**
     * 绘制刻度
     */
    private void drawScale(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xBF3F6AB5);
        paint.setStrokeWidth(1 * mDensity);
        for (int i = 0; i < 60; i++) {
            if (i % 6 == 0) {
                canvas.drawLine(mPointX - mRadius + 6 * mDensity, mPointY, mPointX - mRadius + 25 * mDensity, mPointY, paint);
            } else {
                canvas.drawLine(mPointX - mRadius + 6 * mDensity, mPointY, mPointX - mRadius + 15 * mDensity, mPointY, paint);
            }
            canvas.rotate(6, mPointX, mPointY);
        }
    }

    /**
     * 绘制速度标识文字
     */
    private void drawText(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(15 * mDensity);
        int baseX = 0;
        int baseY = 0;
        //文字的偏移量
        float textScale = Math.abs(paint.descent() + paint.ascent()) / 2;
        for (int i = 0; i < 8; i++) {
            String text = String.valueOf(i * 30);
            switch (i * 30) {
                case 0:
                    // 计算Baseline绘制的起点X轴坐标
                    baseX = (int) (mPointX - (mRadius - 25 * mDensity) * Math.cos(Math.PI / 5) + paint.measureText(text) / 2 + textScale / 2);
                    // 计算Baseline绘制的Y坐标
                    baseY = (int) (mPointY + (mRadius - 25 * mDensity) * Math.sin(Math.PI / 5) + textScale / 2);

                    canvas.drawText(text, baseX, baseY, paint);
                    break;
                case 30:
                    baseX = (int) (mPointX - mRadius + 25 * mDensity + paint.measureText(text) / 2);
                    baseY = (int) (mPointY + textScale);
                    canvas.drawText(text, baseX, baseY, paint);
                    break;
                case 60:
                    break;
                case 90:
                    break;
                case 120:
                    break;
                case 150:
                    break;
                case 180:
                    baseX = (int) (mPointX + mRadius - paint.measureText(text) - textScale / 2);
                    baseY = (int) (mPointY + textScale);
                    canvas.drawText(text, baseX, baseY, paint);
                    break;
                case 210:
                    break;
            }
        }

    }
}
