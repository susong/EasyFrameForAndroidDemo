package com.dream.demo.net.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.dream.demo.net.ConnectStatus;
import com.dream.demo.net.ConnectStatusListener;
import com.dream.demo.net.DataConvert;
import com.dream.demo.net.ReceiveListener;

import java.io.IOException;
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

    private static final String                     TAG             = "UDPBroadcast";
    private              WifiManager.MulticastLock  lock            = null;
    private              int                        listenPort      = 10006;
    private              int                        sendPort        = 10007;
    private              InetAddress                sendAddress     = null;
    private              ReceiveListener            receiveListener = null;
    private              ConnectStatusListener      statusListener  = null;
    private              UDPBroadcast.ReceiveThread receiveThread   = null;
    private              boolean                    isRunning       = false;

    public UDPBroadcast(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.lock = manager.createMulticastLock("UDPwifi");
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public void setSendPort(int sendPort) {
        this.sendPort = sendPort;
    }

    public void setInfo(int listenPort, int sendPort) {
        this.listenPort = listenPort;
        this.sendPort = sendPort;
    }

    public void setReceiveListener(ReceiveListener listener) {
        this.receiveListener = listener;
    }

    public void setStatusListener(ConnectStatusListener listener) {
        this.statusListener = listener;
    }

    public void start() {
        this.isRunning = true;
        this.receiveThread = new UDPBroadcast.ReceiveThread();
        this.receiveThread.start();
    }

    public void stop() {
        this.isRunning = false;
        if (this.receiveThread != null) {
            this.receiveThread.interrupt();
        }
    }

    public void write(byte[] cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "发送：" + DataConvert.Bytes2HexString(cmd, true));
                try {
                    //广播地址
                    sendAddress = InetAddress.getByName("255.255.255.255");
                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    socket.send(new DatagramPacket(cmd, cmd.length, sendAddress, sendPort));
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
                datagramSocket.bind(new InetSocketAddress(UDPBroadcast.this.listenPort));
                if (UDPBroadcast.this.statusListener != null) {
                    UDPBroadcast.this.statusListener.onConnectStatusChanged(UDPBroadcast.this, ConnectStatus.Connected);
                }

                Log.d(TAG, "UDP 启动接收...");
                byte[]         buf            = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

                try {
                    while (UDPBroadcast.this.isRunning) {
                        UDPBroadcast.this.lock.acquire();

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (SocketTimeoutException e) {
                            continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }

                        byte[] e1      = datagramPacket.getData();
                        int    dataLen = datagramPacket.getLength();
                        byte[] data    = new byte[dataLen];
                        System.arraycopy(e1, 0, data, 0, dataLen);
                        Log.e(TAG, "接收：" + datagramPacket.getAddress().getHostAddress() + ":" + DataConvert.Bytes2HexString(data, true));
                        UDPBroadcast.this.lock.release();
                        if (UDPBroadcast.this.receiveListener != null) {
                            UDPBroadcast.this.receiveListener.onNetReceive(UDPBroadcast.this, data);
                        }
                    }

                    Log.e(TAG, "UDP 接收功能结束");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (UDPBroadcast.this.isRunning && UDPBroadcast.this.statusListener != null) {
                        UDPBroadcast.this.statusListener.onConnectStatusChanged(UDPBroadcast.this, ConnectStatus.Error);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (datagramSocket != null) {
                    datagramSocket.close();
                }

                Log.d(TAG, "UDP Close");
            }

        }
    }
}
