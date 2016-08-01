package com.dream.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/21 上午11:35
 * Description: Android 2D绘图解析之 Path
 * http://blog.csdn.net/leejizhou/article/details/51565057
 */
public class CustomPath1View extends View {

    public CustomPath1View(Context context) {
        super(context);
    }

    public CustomPath1View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置绘制风格
        Paint paint = getViewPaint();
        //构建Path路径
        Path path = new Path();
        //设置path的起点位置
        path.moveTo(200, 200);
        //path路径连接到某点
        path.lineTo(100, 400);
        path.lineTo(300, 400);
        //path路径的最后一个点与起点连接，如果不写这句三角形就会变成LEXUS的车标了
        path.close();
        //绘制三角形
        canvas.drawPath(path, paint);

        //如果这里不设置moveTo那么path将汕头上面那个三角形路径最后一点进行继续绘制
        path.moveTo(100, 800);
        path.lineTo(200, 500);
        path.lineTo(300, 800);
        path.lineTo(400, 500);
        path.lineTo(500, 800);
        //绘制M形
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
