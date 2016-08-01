package com.dream.demo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/21 下午2:42
 * Description: Android 2D绘图解析之 Path
 * http://blog.csdn.net/leejizhou/article/details/51565057
 */
public class CustomPath3View extends View {

    public CustomPath3View(Context context) {
        super(context);
    }

    public CustomPath3View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置绘制风格
        Paint paint = getViewPaint();
        //构建Path路径
        Path path = new Path();
        //添加弧形到path
        path.addArc(100, 100, 300, 300, 0, 270);
        //添加圆形到path
        path.addCircle(200, 500, 100, Path.Direction.CCW);
        //添加矩形到path
        path.addRect(100, 700, 300, 800, Path.Direction.CW);
        //添加椭圆到path
        path.addOval(100, 900, 300, 1000, Path.Direction.CCW);
        //绘制path
        canvas.drawPath(path, paint);
    }

    public Paint getViewPaint() {
        //绘制风格
        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        //设置绘制颜色
        paint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        //为了方便看Path的路径效果
        //设置绘制风格为空心
        paint.setStyle(Paint.Style.STROKE);
        //设置空心边框的宽度
        paint.setStrokeWidth(10);
        return paint;
    }
}
