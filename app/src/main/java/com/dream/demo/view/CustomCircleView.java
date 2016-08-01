package com.dream.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/13 下午5:13
 * Description: Android 2D绘图解析之 Canvas，Paint
 * http://blog.csdn.net/leejizhou/article/details/51524948
 */
public class CustomCircleView extends View {

    public CustomCircleView(Context context) {
        super(context);
    }

    public CustomCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        //设置颜色
        paint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        //设置实心Style
        paint.setStyle(Paint.Style.FILL);
        //绘制普通圆
        canvas.drawCircle(100, 100, 100, paint);

        //设置空心Style
        paint.setStyle(Paint.Style.STROKE);
        //设置空心边框的宽度
        paint.setStrokeWidth(20);
        //绘制空心圆
        canvas.drawCircle(100, 300, 90, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(20);
        canvas.drawCircle(100, 500, 90, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(40);
        canvas.drawCircle(100, 700, 80, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(40);
        canvas.drawCircle(100, 900, 80, paint);

    }
}
