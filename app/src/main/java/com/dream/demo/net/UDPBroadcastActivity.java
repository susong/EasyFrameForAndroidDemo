package com.dream.demo.net;

import android.os.Bundle;

import com.dream.demo.R;
import com.dream.demo.activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/28 下午3:37
 * Description: EasyFrameForAndroidDemo
 */
public class UDPBroadcastActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_broadcast);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }
}
