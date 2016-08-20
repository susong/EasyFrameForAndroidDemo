package com.dream.demo.net;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dream.demo.R;
import com.dream.demo.activity.BaseActivity;
import com.dream.demo.net.interf.ReceiveListener;
import com.dream.demo.net.tcp.TcpServer;
import com.dream.demo.net.udp.UDPSearchBroadcastServer;
import com.dream.library.utils.AbLog;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/8/20 上午11:16
 * Description: EasyFrameForAndroidDemo
 */
public class UdpTcpServerActivity extends BaseActivity {

    @BindView(R.id.btn_server_start) Button                   mBtnServerStart;
    @BindView(R.id.btn_server_stop)  Button                   mBtnServerStop;
    private                          TcpServer                mTcpServer;
    private                          UDPSearchBroadcastServer mUdpSearchBroadcastServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_tcp_server);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.btn_server_start, R.id.btn_server_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_server_start:
                serverStart();
                break;
            case R.id.btn_server_stop:
                serverStop();
                break;
        }
    }

    private void serverStart() {
        // 启动广播接收器
        if (mUdpSearchBroadcastServer == null) {
            mUdpSearchBroadcastServer = new UDPSearchBroadcastServer.Builder().build();
        }

        mUdpSearchBroadcastServer.search();

        if (mTcpServer == null) {
            mTcpServer = new TcpServer.Builder().receiverListener(new ReceiveListener() {
                @Override
                public void onNetReceive(Object obj, byte[] bytes) {
                    try {
                        String s = new String(bytes, "utf-8");
                        AbLog.d("server receive:" + s);

                        mTcpServer.send((String) obj, s + "【我收到啦】");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }).build();
        }

        mTcpServer.start();
    }

    private void serverStop() {
        if (mUdpSearchBroadcastServer != null) {
            mUdpSearchBroadcastServer.stop();
        }
        if (mTcpServer != null) {
            mTcpServer.stop();
        }
    }
}
