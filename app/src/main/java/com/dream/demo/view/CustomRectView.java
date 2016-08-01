package com.dream.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/18 下午9:04
 * Description: Android 2D绘图解析之 Canvas，Paint
 * http://blog.csdn.net/leejizhou/article/details/51524948
 */
public class CustomRectView extends View {

    public CustomRectView(Context context) {
        super(context);
    }

    public CustomRectView(Context context, AttributeSet attrs) {
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
        //绘制正方形
        canvas.drawRect(new RectF(100, 100, 300, 300), paint);
        //上面代码等同于
        //RectF rel=new RectF(100,100,300,300);
        //canvas.drawRect(rel, paint);

        //设置空心Style
        paint.setStyle(Paint.Style.STROKE);
        //设置空心边框的宽度
        paint.setStrokeWidth(20);
        //绘制空心矩形
        canvas.drawRect(100, 400, 600, 800, paint);
    }
}
