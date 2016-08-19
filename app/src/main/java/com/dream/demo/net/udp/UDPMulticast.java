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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午2:11
 * Description: UDP多播发送与接收
 */
public class UDPMulticast {

    private static final String                     TAG                    = "UDPMulticast";
    private              WifiManager.MulticastLock  mLock                  = null;
    private              int                        mReceiverPort          = 10006;
    private              int                        mSendPort              = 10007;
    private              InetAddress                mSendAddress           = null;
    private              ReceiveListener            mReceiveListener       = null;
    private              ConnectStatusListener      mConnectStatusListener = null;
    private              UDPMulticast.ReceiveThread mReceiveThread         = null;
    private              boolean                    mIsRunning             = false;

    public UDPMulticast(Context context) {
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
        this.mReceiveThread = new UDPMulticast.ReceiveThread();
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
            Log.d(TAG, "UDP多播发送 " + "端口：" + mSendPort + " 数据：" + msg);
            send(msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "UDP多播发送 " + "端口：" + mSendPort + " 数据：" + DataConvert.Bytes2HexString(cmd, true));
                try {
                    // 多播地址
                    mSendAddress = InetAddress.getByName("224.0.0.1");
                    MulticastSocket socket = new MulticastSocket(mSendPort);
                    socket.joinGroup(mSendAddress);
                    socket.setTimeToLive(5);
                    socket.send(new DatagramPacket(cmd, cmd.length, mSendAddress, mSendPort));
                    socket.leaveGroup(mSendAddress);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class ReceiveThread extends Thread {

        public void run() {
            MulticastSocket datagramSocket = null;
            try {
                // 多播地址
                mSendAddress = InetAddress.getByName("224.0.0.1");
                datagramSocket = new MulticastSocket(mReceiverPort);
                datagramSocket.joinGroup(mSendAddress);
                if (mConnectStatusListener != null) {
                    mConnectStatusListener.onConnectStatusChanged(UDPMulticast.this, ConnectStatus.Connected);
                }

                Log.d(TAG, "UDP多播 端口：" + mReceiverPort + "  启动接收...");
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
                        Log.d(TAG, "UDP多播接收：" + datagramPacket.getAddress().getHostAddress() + ":" + new String(dataNew, "utf-8"));
                        Log.d(TAG, "UDP多播接收：" + datagramPacket.getAddress().getHostAddress() + ":" + DataConvert.Bytes2HexString(dataNew, true));
                        mLock.release();
                        if (mReceiveListener != null) {
                            mReceiveListener.onNetReceive(UDPMulticast.this, dataNew);
                        }
                    }

                    Log.e(TAG, "UDP多播 接收功能结束");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mIsRunning && mConnectStatusListener != null) {
                        mConnectStatusListener.onConnectStatusChanged(UDPMulticast.this, ConnectStatus.Error);
                    }
                }
                datagramSocket.leaveGroup(mSendAddress);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
                Log.d(TAG, "UDP多播 Close");
            }
        }
    }
}
