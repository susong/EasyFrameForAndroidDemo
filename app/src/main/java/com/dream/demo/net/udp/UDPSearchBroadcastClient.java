package com.dream.demo.net.udp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.dream.library.utils.AbLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 搜索广播
 * author True Lies
 * Created by Administrator on 2016/8/8.
 */
@SuppressWarnings("unused")
@SuppressLint("LongLogTag")
public class UDPSearchBroadcastClient {

    private static final String  TAG   = "UDPSearchBroadcastServer";
    private static       boolean DEBUG = true;
    private Context            mContext;
    private int                mPort;
    private String             mRequest;
    private String             mReceive;
    private onReceiverListener mOnReceiverListener;
    private boolean mIsStop = true;
    private DatagramSocket mDatagramSocket;

    public UDPSearchBroadcastClient(Builder builder) {
        this.mContext = builder.mContext;
        this.mPort = builder.mPort;
        this.mRequest = builder.mRequest;
        this.mReceive = builder.mReceive;
        this.mOnReceiverListener = builder.mOnReceiverListener;
    }

    public interface onReceiverListener {
        void onReceiver(String msg);
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
                AbLog.i(TAG, "Client UDP搜索已经启动");
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
            AbLog.i(TAG, "Client 停止UDP搜索");
        }
    }

    private void searchUDP() {
        try {
            if (DEBUG) {
                AbLog.i(TAG, "Client 启动UDP搜索");
            }
            mIsStop = false;
            mDatagramSocket = new DatagramSocket();
            mDatagramSocket.setReuseAddress(true);
            //mDatagramSocket.setSoTimeout(1000);

            new Thread(new ReceiverRunnable(mDatagramSocket)).start();

            byte[] requestBytes     = mRequest.getBytes("utf-8");
            String broadcastAddress = getBroadcastAddress(mContext);
            DatagramPacket requestDatagramPacket = new DatagramPacket(requestBytes, requestBytes.length,
                                                                      new InetSocketAddress(broadcastAddress, mPort));
            while (!mIsStop) {
                if (DEBUG) {
                    AbLog.i(TAG, "Client 发送UDP搜索：" + mRequest);
                }
                mDatagramSocket.send(requestDatagramPacket);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            if (DEBUG) {
                AbLog.e(TAG, "Client UDP搜索出错");
            }
            mIsStop = true;
            e.printStackTrace();
        }
    }

    /**
     * 线程任务 负责接收数据
     */
    private class ReceiverRunnable implements Runnable {

        private DatagramSocket mDatagramSocket = null;

        public ReceiverRunnable(DatagramSocket datagramSocket) {
            this.mDatagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            try {
                byte[]         receiveBytes          = new byte[1024];
                DatagramPacket receiveDatagramPacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                while (!mIsStop) {
                    if (DEBUG) {
                        AbLog.i(TAG, "Client 开始UDP搜索");
                    }
                    // 阻塞线程
                    mDatagramSocket.receive(receiveDatagramPacket);
                    String receive = new String(receiveDatagramPacket.getData(), receiveDatagramPacket.getOffset(), receiveDatagramPacket.getLength(), "UTF-8");
                    if (DEBUG) {
                        AbLog.i(TAG, "Client UDP搜索到数据：" + receive);
                    }
                    if (!TextUtils.isEmpty(receive) && receive.equals(mReceive)) {
                        String hostAddress = receiveDatagramPacket.getAddress().getHostAddress();
                        if (DEBUG) {
                            AbLog.i(TAG, "Client UDP搜索到的服务器地址：" + hostAddress);
                        }
                        if (mOnReceiverListener != null) {
                            mOnReceiverListener.onReceiver(hostAddress);
                        }
                        stop();
                    }
                }
            } catch (Exception e) {
                if (DEBUG && !mIsStop) {
                    AbLog.e(TAG, "Client UDP搜索出错", e);
                }
            }
        }
    }

    /**
     * 获取广播地址
     */
    public static String getBroadcastAddress(Context context) {
        try {
            String              currentBroadcastIp = null;
            ConnectivityManager manager            = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo         info               = manager.getActiveNetworkInfo();
            int                 network_type       = info.getType();
            if (network_type == 1) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                DhcpInfo    dhcp        = wifiManager.getDhcpInfo();
                int         broadcast   = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
                byte[]      quads       = new byte[4];
                for (int k = 0; k < 4; k++) {
                    quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
                    currentBroadcastIp = InetAddress.getByAddress(quads).getHostAddress();
                }
            }
            return currentBroadcastIp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder {
        private Context mContext = null;
        private int     mPort    = 6666;
        private String  mRequest = "receive";
        private String  mReceive = "response";
        private onReceiverListener mOnReceiverListener;

        public Builder port(int port) {
            this.mPort = port;
            return this;
        }

        public Builder request(String request) {
            this.mRequest = request;
            return this;
        }

        public Builder receive(String receive) {
            this.mReceive = receive;
            return this;
        }

        public Builder onReceiverListener(onReceiverListener onReceiverListener) {
            this.mOnReceiverListener = onReceiverListener;
            return this;
        }

        public UDPSearchBroadcastClient build(Context context) {
            this.mContext = context;
            return new UDPSearchBroadcastClient(this);
        }
    }
}
