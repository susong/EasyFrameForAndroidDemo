package com.dream.demo.net.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.dream.demo.net.interf.ConnectStatus;
import com.dream.demo.net.interf.ConnectStatusListener;
import com.dream.demo.net.utils.DataConvert;
import com.dream.demo.net.interf.ReceiveListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午2:11
 * Description: UDP广播发送与接收
 */
public class UDPBroadcast {

    private static final String                     TAG                    = "UDPBroadcast";
    private              WifiManager.MulticastLock  mLock                  = null;
    private              int                        mReceiverPort          = 10006;
    private              int                        mSendPort              = 10007;
    private              InetAddress                mSendAddress           = null;
    private              ReceiveListener            mReceiveListener       = null;
    private              ConnectStatusListener      mConnectStatusListener = null;
    private              UDPBroadcast.ReceiveThread mReceiveThread         = null;
    private              boolean                    mIsRunning             = false;

    public UDPBroadcast(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.mLock = manager.createMulticastLock("UDPwifi");
    }

    public void setReceiverPort(int receiverPort) {
        this.mReceiverPort = receiverPort;
    }

    public void setSendPort(int sendPort) {
        this.mSendPort = sendPort;
    }

    public void setInfo(int listenPort, int sendPort) {
        this.mReceiverPort = listenPort;
        this.mSendPort = sendPort;
    }

    public void setReceiveListener(ReceiveListener listener) {
        this.mReceiveListener = listener;
    }

    public void setConnectStatusListener(ConnectStatusListener listener) {
        this.mConnectStatusListener = listener;
    }

    public void start() {
        this.mIsRunning = true;
        this.mReceiveThread = new UDPBroadcast.ReceiveThread();
        this.mReceiveThread.start();
    }

    public void stop() {
        this.mIsRunning = false;
        if (this.mReceiveThread != null) {
            this.mReceiveThread.interrupt();
        }
    }

    public void send(String msg) {
        try {
            Log.d(TAG, "UDP广播发送 " + "端口：" + mSendPort + " 数据：" + msg);
            send(msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "UDP广播发送 " + "端口：" + mSendPort + " 数据：" + DataConvert.Bytes2HexString(cmd, true));
                    //广播地址
                    mSendAddress = InetAddress.getByName("255.255.255.255");
                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    socket.send(new DatagramPacket(cmd, cmd.length, mSendAddress, mSendPort));
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class ReceiveThread extends Thread {

        public void run() {
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket = new DatagramSocket(null);
                datagramSocket.setBroadcast(true);
                datagramSocket.setSoTimeout(10000);
                datagramSocket.setReuseAddress(true);
                datagramSocket.bind(new InetSocketAddress(mReceiverPort));
                if (mConnectStatusListener != null) {
                    mConnectStatusListener.onConnectStatusChanged(UDPBroadcast.this, ConnectStatus.Connected);
                }

                Log.d(TAG, "UDP广播 " + "端口：" + mReceiverPort + " 启动接收...");
                byte[]         buf            = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

                try {
                    while (mIsRunning) {
                        mLock.acquire();

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (SocketTimeoutException e) {
                            continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }

                        byte[] data    = datagramPacket.getData();
                        int    dataLen = datagramPacket.getLength();
                        byte[] dataNew = new byte[dataLen];
                        System.arraycopy(data, 0, dataNew, 0, dataLen);
                        Log.d(TAG, "UDP广播接收：" + datagramPacket.getAddress().getHostAddress() + ":" + mReceiverPort + " 数据" + new String(dataNew, "utf-8"));
                        Log.d(TAG, "UDP广播接收：" + datagramPacket.getAddress().getHostAddress() + ":" + mReceiverPort + " 数据" + DataConvert.Bytes2HexString(dataNew, true));
                        mLock.release();
                        if (mReceiveListener != null) {
                            mReceiveListener.onNetReceive(UDPBroadcast.this, dataNew);
                        }
                    }

                    Log.e(TAG, "UDP广播 接收功能结束");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mIsRunning && mConnectStatusListener != null) {
                        mConnectStatusListener.onConnectStatusChanged(UDPBroadcast.this, ConnectStatus.Error);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
                Log.e(TAG, "UDP广播 Close");
            }
        }
    }
}
