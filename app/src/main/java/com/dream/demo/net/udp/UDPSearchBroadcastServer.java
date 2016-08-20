package com.dream.demo.net.udp;


import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.dream.library.utils.AbLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 广播设置
 */
@SuppressWarnings("unused")
@SuppressLint("LongLogTag")
public class UDPSearchBroadcastServer {

    private static final String  TAG   = "UDPSearchBroadcastServer";
    private static       boolean DEBUG = true;
    private int                mPort;
    private String             mReceive;
    private String             mResponse;
    private onReceiverListener mOnReceiverListener;
    private boolean mIsStop = true;
    private DatagramSocket mDatagramSocket;

    private UDPSearchBroadcastServer(Builder builder) {
        this.mPort = builder.mPort;
        this.mReceive = builder.mReceive;
        this.mResponse = builder.mResponse;
        this.mOnReceiverListener = builder.mOnReceiverListener;
    }

    public interface onReceiverListener {
        String onReceiver(String msg);
    }

    public void search() {
        if (mIsStop) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchUDP();
                }
            }).start();
        } else {
            if (DEBUG) {
                AbLog.i(TAG, "Server UDP搜索已经启动");
            }
        }
    }

    public void stop() {
        mIsStop = true;
        if (mDatagramSocket != null) {
            if (mDatagramSocket.isConnected()) {
                mDatagramSocket.disconnect();
            }
            if (!mDatagramSocket.isClosed()) {
                mDatagramSocket.close();
            }
            mDatagramSocket = null;
        }
        if (DEBUG) {
            AbLog.i(TAG, "Server 停止UDP搜索");
        }
    }

    /**
     * 服务器设置
     */
    private void searchUDP() {
        try {
            if (DEBUG) {
                AbLog.i(TAG, "Server 启动UDP搜索");
            }
            mIsStop = false;
            mDatagramSocket = new DatagramSocket(mPort);
            byte[]         receiveBytes          = new byte[1024];
            DatagramPacket receiveDatagramPacket = new DatagramPacket(receiveBytes, receiveBytes.length);
            DatagramPacket sendDatagramPacket    = new DatagramPacket(mResponse.getBytes(), 0, mResponse.getBytes().length);
            while (!mIsStop) {
                if (DEBUG) {
                    AbLog.i(TAG, "Server 开始UDP搜索");
                }
                // 阻塞线程
                mDatagramSocket.receive(receiveDatagramPacket);
                String receive = new String(receiveDatagramPacket.getData(), 0, receiveDatagramPacket.getLength());
                if (DEBUG) {
                    AbLog.i(TAG, "Server UDP搜索到数据：" + receive);
                }
                if (!TextUtils.isEmpty(receive) && receive.equals(mReceive)) {
                    if (mOnReceiverListener != null) {
                        String response = mOnReceiverListener.onReceiver(receive);
                        if (!TextUtils.isEmpty(response)) {
                            sendDatagramPacket = new DatagramPacket(response.getBytes(), 0, response.getBytes().length);
                        }
                    }
                    sendDatagramPacket.setSocketAddress(receiveDatagramPacket.getSocketAddress());
                    if (DEBUG) {
                        AbLog.i(TAG, "Server UDP返回数据：" + mResponse);
                    }
                    mDatagramSocket.send(sendDatagramPacket);
                }
            }
        } catch (Exception e) {
            if (DEBUG && !mIsStop) {
                AbLog.e(TAG, "Server UDP搜索出错", e);
            }
            mIsStop = true;
        }
    }

    public static class Builder {
        private int    mPort     = 6666;
        private String mReceive  = "receive";
        private String mResponse = "response";
        private onReceiverListener mOnReceiverListener;

        public Builder port(int port) {
            this.mPort = port;
            return this;
        }

        public Builder receive(String receive) {
            this.mReceive = receive;
            return this;
        }

        public Builder response(String response) {
            this.mResponse = response;
            return this;
        }

        public Builder onReceiverListener(onReceiverListener onReceiverListener) {
            this.mOnReceiverListener = onReceiverListener;
            return this;
        }

        public UDPSearchBroadcastServer build() {
            return new UDPSearchBroadcastServer(this);
        }
    }
}
