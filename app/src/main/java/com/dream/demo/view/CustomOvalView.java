package com.dream.demo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/20 下午11:27
 * Description: Android 2D绘图解析之 Canvas，Paint
 * http://blog.csdn.net/leejizhou/article/details/51524948
 */
public class CustomOvalView extends View {

    public CustomOvalView(Context context) {
        super(context);
    }

    public CustomOvalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        //设置颜色
        paint.setColor(getResources().getColor(android.R.color.holo_orange_dark));
        //绘制椭圆
        canvas.drawOval(100, 100, 500, 300, paint);

        //设置空心Style
        paint.setStyle(Paint.Style.STROKE);
        //设置空心边框的宽度
        paint.setStrokeWidth(20);
        //绘制空心椭圆
        canvas.drawOval(100, 400, 600, 800, paint);
    }
}
