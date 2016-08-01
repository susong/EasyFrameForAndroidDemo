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
 * Date:        16/7/21 下午3:34
 * Description: Android 2D绘图解析之 Path
 * http://blog.csdn.net/leejizhou/article/details/51565057
 * <p>
 * Path.Op有如下几种参数：
 * Path.Op.DIFFERENCE：减去Path2后Path1剩下的部分
 * Path.Op.INTERSECT：保留Path1与Path2共同的部分
 * Path.Op.REVERSE_DIFFERENCE：减去Path1后Path2剩下的部分
 * Path.Op.UNION：保留全部Path1和Path2
 * Path.Op.XOR：包含Path1与Path2但不包括两者相交的部分
 */
public class CustomPath4View extends View {

    public CustomPath4View(Context context) {
        super(context);
    }

    public CustomPath4View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置绘制风格
        Paint paint = getViewPaint();
        //设置填充风格，方便观察效果
        paint.setStyle(Paint.Style.FILL);
        //构建Path路径
        Path path = new Path();
        path.addCircle(150, 150, 100, Path.Direction.CW);
        Path path2 = new Path();
        path2.addCircle(300, 150, 100, Path.Direction.CW);
        path.op(path2, Path.Op.UNION);
        canvas.drawPath(path, paint);

        //清除路径
        path.reset();
        path2.reset();
        path.addCircle(150, 400, 100, Path.Direction.CW);
        path2.addCircle(300, 400, 100, Path.Direction.CW);
        path.op(path2, Path.Op.REVERSE_DIFFERENCE);
        canvas.drawPath(path, paint);

        //清除路径
        path.reset();
        path2.reset();
        path.addCircle(150, 650, 100, Path.Direction.CW);
        path2.addCircle(300, 650, 100, Path.Direction.CW);
        path.op(path2, Path.Op.INTERSECT);
        canvas.drawPath(path, paint);

        //清除路径
        path.reset();
        path2.reset();
        path.addCircle(150, 900, 100, Path.Direction.CW);
        path2.addCircle(300, 900, 100, Path.Direction.CW);
        path.op(path2, Path.Op.DIFFERENCE);
        canvas.drawPath(path, paint);

        //清除路径
        path.reset();
        path2.reset();
        path.addCircle(150, 1150, 100, Path.Direction.CW);
        path2.addCircle(300, 1150, 100, Path.Direction.CW);
        path.op(path2, Path.Op.XOR);
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
