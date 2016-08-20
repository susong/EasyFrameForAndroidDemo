package com.dream.demo.net;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dream.demo.R;
import com.dream.demo.activity.BaseActivity;
import com.dream.demo.net.interf.ReceiveListener;
import com.dream.demo.net.tcp.TcpClient;
import com.dream.demo.net.udp.UDPSearchBroadcastClient;
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
public class UdpTcpClientActivity extends BaseActivity {

    @BindView(R.id.btn_client_start) Button                   mBtnClientStart;
    @BindView(R.id.btn_client_send)  Button                   mBtnClientSend;
    @BindView(R.id.btn_client_stop)  Button                   mBtnClientStop;
    private                          TcpClient                mTcpClient;
    private                          UDPSearchBroadcastClient mUdpSearchBroadcastClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_tcp_client);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_client_start, R.id.btn_client_send, R.id.btn_client_stop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_client_start:
                clientStart();
                break;
            case R.id.btn_client_send:
                clientSend();
                break;
            case R.id.btn_client_stop:
                clientStop();
                break;
        }
    }

    private void clientStart() {
        if (mUdpSearchBroadcastClient == null) {
            mUdpSearchBroadcastClient = new UDPSearchBroadcastClient.Builder().onReceiverListener(new UDPSearchBroadcastClient.onReceiverListener() {
                @Override
                public void onReceiver(String msg) {
                    if (mTcpClient != null) {
                        AbLog.d("mTcpClient != null");
                        mTcpClient.stop();
                    }
                    mTcpClient = new TcpClient.Builder().host(msg).receiverListener(new ReceiveListener() {
                        @Override
                        public void onNetReceive(Object obj, byte[] bytes) {
                            try {
                                String s = new String(bytes, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }
                    }).build();
                    mTcpClient.start();
                }
            }).build(this);
        }
        mUdpSearchBroadcastClient.search();
    }

    private void clientSend() {
        if (mTcpClient != null) {
            mTcpClient.send("你好，哈哈");
        }
    }

    private void clientStop() {
        if (mUdpSearchBroadcastClient != null) {
            mUdpSearchBroadcastClient.stop();
        }
        if (mTcpClient != null) {
            mTcpClient.stop();
        }
    }


}
