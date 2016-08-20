package com.dream.demo.net;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dream.demo.R;
import com.dream.demo.activity.BaseActivity;
import com.dream.demo.net.udp.UDPSearchBroadcastClient;
import com.dream.demo.net.udp.UDPSearchBroadcastServer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/8/19 下午8:18
 * Description: EasyFrameForAndroidDemo
 */
public class UdpSearchActivity extends BaseActivity {

    @BindView(R.id.btn_server_start) Button                   mBtnServerStart;
    @BindView(R.id.btn_server_stop)  Button                   mBtnServerStop;
    @BindView(R.id.btn_client_start) Button                   mBtnClientStart;
    @BindView(R.id.btn_client_stop)  Button                   mBtnClientStop;
    private                          UDPSearchBroadcastServer mUDPSearchBroadcastServer;
    private                          UDPSearchBroadcastClient mUDPSearchBroadcastClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_search);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_server_start, R.id.btn_server_stop, R.id.btn_client_start, R.id.btn_client_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_server_start:
                if (mUDPSearchBroadcastServer == null) {
                    mUDPSearchBroadcastServer = new UDPSearchBroadcastServer.Builder().build();
                }
                mUDPSearchBroadcastServer.search();
                break;
            case R.id.btn_server_stop:
                if (mUDPSearchBroadcastServer != null) {
                    mUDPSearchBroadcastServer.stop();
                }
                break;
            case R.id.btn_client_start:
                if (mUDPSearchBroadcastClient == null) {
                    mUDPSearchBroadcastClient = new UDPSearchBroadcastClient.Builder().build(this);
                }
                mUDPSearchBroadcastClient.search();
                break;
            case R.id.btn_client_stop:
                if (mUDPSearchBroadcastClient != null) {
                    mUDPSearchBroadcastClient.stop();
                }
                break;
        }
    }
}
