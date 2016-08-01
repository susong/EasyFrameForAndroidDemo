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
 * Date:        16/7/20 下午11:30
 * Description: Android 2D绘图解析之 Canvas，Paint
 * http://blog.csdn.net/leejizhou/article/details/51524948
 */
public class CustomArcView extends View {

    public CustomArcView(Context context) {
        super(context);
    }

    public CustomArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        //设置颜色
        paint.setColor(getResources().getColor(android.R.color.holo_orange_dark));

        //实心圆弧
        RectF rel = new RectF(100, 100, 300, 300);
        canvas.drawArc(rel, 0, 270, false, paint);

        //实心圆弧 将圆心包含在内
        RectF rel2 = new RectF(100, 400, 300, 600);
        canvas.drawArc(rel2, 0, 270, true, paint);

        //实心圆弧
        RectF rel5 = new RectF(400, 100, 600, 300);
        canvas.drawArc(rel5, 270, 90, false, paint);

        //实心圆弧 将圆心包含在内
        RectF rel6 = new RectF(400, 400, 600, 600);
        canvas.drawArc(rel6, 270, 90, true, paint);

        //实心圆弧 将圆心包含在内
        RectF rel7 = new RectF(400, 700, 600, 900);
        canvas.drawArc(rel7, 0, 120, true, paint);

        //设置空心Style
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        RectF rel3 = new RectF(100, 700, 300, 900);
        canvas.drawArc(rel3, 0, 270, false, paint);

        RectF rel4 = new RectF(100, 1000, 300, 1200);
        canvas.drawArc(rel4, 0, 270, true, paint);
    }
}
