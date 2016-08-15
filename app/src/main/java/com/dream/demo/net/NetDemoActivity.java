package com.dream.demo.net;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dream.demo.R;
import com.dream.demo.net.udp.UDPBroadcast;
import com.dream.library.utils.AbLog;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/26 上午11:15
 * Description: EasyFrameForAndroidDemo
 */
public class NetDemoActivity extends AppCompatActivity {

    @BindView(R.id.et_udp_broadcast_send_port)    EditText mEtUdpBroadcastSendPort;
    @BindView(R.id.btn_udp_broadcast_send)        Button   mBtnUdpBroadcastSend;
    @BindView(R.id.et_udp_broadcast_receive_port) EditText mEtUdpBroadcastReceivePort;
    @BindView(R.id.btn_udp_broadcast_receive)     Button   mBtnUdpBroadcastReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_demo);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_udp_broadcast_send, R.id.btn_udp_broadcast_receive})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_udp_broadcast_send:
                udpBroadcastSend();
                break;
            case R.id.btn_udp_broadcast_receive:
                udpBroadcastReceive();
                break;
        }
    }

    private void udpBroadcastSend() {
        int port = Integer.parseInt(mEtUdpBroadcastSendPort.getText().toString().trim());
        try {
            UDPBroadcast udpBroadcast = new UDPBroadcast(this);
            udpBroadcast.setSendPort(port);
            String data = "Hi~ 你好啊！";
            AbLog.d("发送数据：" + data);
            udpBroadcast.send(data.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void udpBroadcastReceive() {
        int          port         = Integer.parseInt(mEtUdpBroadcastReceivePort.getText().toString().trim());
        UDPBroadcast udpBroadcast = new UDPBroadcast(this);
        udpBroadcast.setReceiverPort(port);
        udpBroadcast.setReceiveListener(new ReceiveListener() {
            @Override
            public void onNetReceive(Object var1, byte[] bytes) {
                AbLog.d("接收到数据：" + new String(bytes, Charset.forName("utf-8")));
            }
        });
    }
}