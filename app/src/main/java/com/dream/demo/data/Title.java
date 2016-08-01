package com.dream.demo.data;

import com.dream.demo.R;
import com.dream.demo.activity.MainActivity;
import com.dream.demo.net.NetActivity;
import com.dream.demo.entitiy.TitleEntity;
import com.dream.demo.view.CustomPath1View;
import com.dream.demo.view.CustomPath2View;
import com.dream.demo.view.CustomPath3View;
import com.dream.demo.view.CustomPath4View;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/20 下午11:02
 * Description: EasyFrameForAndroidDemo
 */
@SuppressWarnings("unused")
public class Title {
    public static TitleEntity title_a_1 = new TitleEntity("", R.layout.view_listview, null);

    public static TitleEntity title_b_1_1 = new TitleEntity("高大上", R.layout.view_listview, MainActivity.class);
    public static TitleEntity title_b_1_2 = new TitleEntity("郭霖", R.layout.view_listview, MainActivity.class);
    public static TitleEntity title_b_1_3 = new TitleEntity("stormzhang", R.layout.view_listview, MainActivity.class);

    public static TitleEntity title_c_1_1_1 = new TitleEntity("UDP广播发送和接收", R.layout.activity_net, NetActivity.class);
    public static TitleEntity title_c_1_1_2 = new TitleEntity("Android UDP广播发送和接收服务", R.layout.activity_main, com.dream.demo.tcpservice.MainActivity.class);

    public static TitleEntity title_c_1_2_1 = new TitleEntity("Android绘图技术详解，带你轻松绘出各种图形", R.layout.view_listview, MainActivity.class);
    public static TitleEntity title_c_1_2_2 = new TitleEntity("Android绘图进阶之Path详解", R.layout.view_listview, MainActivity.class);
    public static TitleEntity title_c_1_2_3 = new TitleEntity("自定义View实战：汽车速度仪表盘", R.layout.view_custom_speed_control, MainActivity.class);
    public static TitleEntity title_c_1_2_4 = new TitleEntity("自定义View实现空调遥控器切换度数", R.layout.view_custom_air_move, MainActivity.class);

    public static TitleEntity title_d_1_2_1_1 = new TitleEntity("圆形", R.layout.view_custom_circle, MainActivity.class);
    public static TitleEntity title_d_1_2_1_2 = new TitleEntity("距形", R.layout.view_custom_rect, MainActivity.class);
    public static TitleEntity title_d_1_2_1_3 = new TitleEntity("圆角矩形", R.layout.view_custom_round_rect, MainActivity.class);
    public static TitleEntity title_d_1_2_1_4 = new TitleEntity("椭圆", R.layout.view_custom_oval, MainActivity.class);
    public static TitleEntity title_d_1_2_1_5 = new TitleEntity("弧", R.layout.view_custom_arc, MainActivity.class);
    public static TitleEntity title_d_1_2_1_6 = new TitleEntity("文字", R.layout.view_custom_text, MainActivity.class);

    public static TitleEntity title_d_1_2_2_1 = new TitleEntity("利用moveTo，lineTo，close方法绘制各种直线图形", CustomPath1View.class, MainActivity.class);
    public static TitleEntity title_d_1_2_2_2 = new TitleEntity("利用arcTo绘制各种弧线图形", CustomPath2View.class, MainActivity.class);
    public static TitleEntity title_d_1_2_2_3 = new TitleEntity("已封装的效果，如圆形，矩形等", CustomPath3View.class, MainActivity.class);
    public static TitleEntity title_d_1_2_2_4 = new TitleEntity("op方法将两个Path路径进行组合", CustomPath4View.class, MainActivity.class);

    //    public static TitleEntity title_e_1_1_1_1_1 = new TitleEntity();
}
