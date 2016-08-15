package com.dream.demo.net;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.dream.demo.R;
import com.dream.demo.activity.BaseActivity;
import com.dream.demo.net.udp.UDPBroadcastReceiver;
import com.dream.demo.net.udp.UDPBroadcastSend;
import com.dream.library.utils.AbLog;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/8/15 上午11:02
 * Description: EasyFrameForAndroidDemo
 */
public class NetActivity extends BaseActivity {

    @BindView(R.id.et_server_ip)             EditText         mEtServerIp;
    @BindView(R.id.et_server_port)           EditText         mEtServerPort;
    @BindView(R.id.btn_start_server)         Button           mBtnStartServer;
    @BindView(R.id.et_server_send_message)   EditText         mEtServerSendMessage;
    @BindView(R.id.btn_server_send)          Button           mBtnServerSend;
    @BindView(R.id.btn_server_clear_message) Button           mBtnServerClearMessage;
    @BindView(R.id.lv_server_msg)            ListView         mLvServerMsg;
    @BindView(R.id.et_client_ip)             EditText         mEtClientIp;
    @BindView(R.id.et_client_port)           EditText         mEtClientPort;
    @BindView(R.id.btn_start_client)         Button           mBtnStartClient;
    @BindView(R.id.et_client_send_message)   EditText         mEtClientSendMessage;
    @BindView(R.id.btn_client_send)          Button           mBtnClientSend;
    @BindView(R.id.btn_client_clear_message) Button           mBtnClientClearMessage;
    @BindView(R.id.lv_client_msg)            ListView         mLvClientMsg;
    private                                  String           mServerIp;
    private                                  String           mServerPort;
    private                                  String           mServerSendMessage;
    private                                  String           mClientIp;
    private                                  String           mClientPort;
    private                                  String           mClientSendMessage;
    private                                  UDPBroadcastSend mUdpBroadcastSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({
                     R.id.btn_start_server,
                     R.id.btn_server_send,
                     R.id.btn_server_clear_message,
                     R.id.btn_start_client,
                     R.id.btn_client_send,
                     R.id.btn_client_clear_message
             })
    public void onClick(View view) {
        getInputInfo();
        switch (view.getId()) {
            case R.id.btn_start_server:

                break;
            case R.id.btn_server_send:
                UDPBroadcastReceiver udpBroadcastReceiver = new UDPBroadcastReceiver(getApplicationContext());
                if (!TextUtils.isEmpty(mServerPort)) {
                    udpBroadcastReceiver.setReceiverPort(Integer.parseInt(mServerPort));
                }
                udpBroadcastReceiver.setReceiveListener(new ReceiveListener() {
                    @Override
                    public void onNetReceive(Object obj, byte[] bytes) {
                        try {
                            AbLog.d(new String(bytes, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                udpBroadcastReceiver.start();
                break;
            case R.id.btn_server_clear_message:
                break;
            case R.id.btn_start_client:
                mUdpBroadcastSend = new UDPBroadcastSend();
                if (!TextUtils.isEmpty(mClientPort)) {
                    mUdpBroadcastSend.setSendPort(Integer.parseInt(mClientPort));
                }
                break;
            case R.id.btn_client_send:
                if (mUdpBroadcastSend != null && !TextUtils.isEmpty(mClientSendMessage)) {
                    mUdpBroadcastSend.send(mClientSendMessage);
                }
                break;
            case R.id.btn_client_clear_message:
                break;
        }
    }

    private void init() {

    }

    private void getInputInfo() {
        mServerIp = mEtServerIp.getText().toString().trim();
        mServerPort = mEtServerPort.getText().toString().trim();
        mServerSendMessage = mEtServerSendMessage.getText().toString().trim();
        mClientIp = mEtClientIp.getText().toString().trim();
        mClientPort = mEtClientPort.getText().toString().trim();
        mClientSendMessage = mEtClientSendMessage.getText().toString().trim();
    }
}
