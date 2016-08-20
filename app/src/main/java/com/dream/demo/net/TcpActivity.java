package com.dream.demo.net;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.dream.demo.R;
import com.dream.demo.activity.BaseActivity;
import com.dream.demo.net.tcp.TcpClient;
import com.dream.demo.net.tcp.TcpServer;
import com.dream.demo.net.udp.UDPBroadcastSend;
import com.dream.demo.net.udp.UDPMulticastSend;
import com.dream.demo.net.utils.WifiManagerUtils;
import com.dream.library.utils.AbLog;

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
public class TcpActivity extends BaseActivity {

    @BindView(R.id.et_server_ip)             EditText         mEtServerIp;
    @BindView(R.id.et_server_port)           EditText         mEtServerPort;
    @BindView(R.id.btn_server_start)         Button           mBtnServerStart;
    @BindView(R.id.et_server_send_message)   EditText         mEtServerSendMessage;
    @BindView(R.id.btn_server_send)          Button           mBtnServerSend;
    @BindView(R.id.btn_server_clear_message) Button           mBtnServerClearMessage;
    @BindView(R.id.lv_server_msg)            ListView         mLvServerMsg;
    @BindView(R.id.et_client_ip)             EditText         mEtClientIp;
    @BindView(R.id.et_client_port)           EditText         mEtClientPort;
    @BindView(R.id.btn_client_start)         Button           mBtnClientStart;
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
    private                                  UDPMulticastSend mUDPMulticastSend;
    private                                  TcpServer        mTcpServer;
    private                                  TcpClient        mTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({
                     R.id.btn_server_start,
                     R.id.btn_server_send,
                     R.id.btn_server_clear_message,
                     R.id.btn_client_start,
                     R.id.btn_client_send,
                     R.id.btn_client_clear_message
             })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_server_start:
//                mTcpServer = new TcpServer();
//                mTcpServer.setPort(Integer.parseInt(mEtServerPort.getText().toString().trim()));
//                mTcpServer.setReceiveListener(new ReceiveListener() {
//                    @Override
//                    public void onNetReceive(Object obj, byte[] bytes) {
//                        try {
//                            String s = obj.toString() + "  " + new String(bytes, "utf-8");
//                            AbLog.d("server onNetReceive:" + s);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                mTcpServer.start();
                break;
            case R.id.btn_server_send:
                if (mTcpServer == null) {
                    return;
                }
                mTcpServer.send(mEtServerSendMessage.getText().toString().trim());
                break;
            case R.id.btn_server_clear_message:
                break;
            case R.id.btn_client_start:
//                mTcpClient = new TcpClient();
//                mTcpClient.setHostAndPort(mEtClientIp.getText().toString().trim(), Integer.parseInt(mEtClientPort.getText().toString().trim()));
//                mTcpClient.setReceiveListener(new ReceiveListener() {
//                    @Override
//                    public void onNetReceive(Object obj, byte[] bytes) {
//                        try {
//                            String s = obj.toString() + " " + new String(bytes, "utf-8");
//                            AbLog.d("client onNetReceive:" + s);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                mTcpClient.start();
                break;
            case R.id.btn_client_send:
                if (mTcpClient == null) {
                    return;
                }
                mTcpClient.send(mEtClientSendMessage.getText().toString().trim());
                break;
            case R.id.btn_client_clear_message:
                break;
        }
    }

    private void init() {

        String localIpAddress1 = WifiManagerUtils.getLocalIpAddress(this);

        String localIpAddress = WifiManagerUtils.getLocalIpAddress();


        String broadcastAddress = WifiManagerUtils.getBroadcastAddress(this);

        String currentIpAddress = WifiManagerUtils.getBroadcastAddress2(this);

        AbLog.d("localIpAddress1:" + localIpAddress1);
        AbLog.d("localIpAddress:" + localIpAddress);
        AbLog.d("broadcastAddress:" + broadcastAddress);
        AbLog.d("currentIpAddress:" + currentIpAddress);
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
