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

    private static final String                     TAG             = "UDPMulticast";
    private              WifiManager.MulticastLock  lock            = null;
    private              int                        listenPort      = 10006;
    private              int                        sendPort        = 10007;
    private              InetAddress                sendAddress     = null;
    private              ReceiveListener            receiveListener = null;
    private              ConnectStatusListener      statusListener  = null;
    private              UDPMulticast.ReceiveThread receiveThread   = null;
    private              boolean                    isRunning       = false;

    public UDPMulticast(Context context) {
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
        this.receiveThread = new UDPMulticast.ReceiveThread();
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
                    // 多播地址
                    sendAddress = InetAddress.getByName("224.0.0.1");
                    MulticastSocket socket = new MulticastSocket(sendPort);
                    socket.joinGroup(sendAddress);
                    socket.setTimeToLive(5);
                    socket.send(new DatagramPacket(cmd, cmd.length, sendAddress, sendPort));
                    socket.leaveGroup(sendAddress);
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
                datagramSocket = new MulticastSocket(UDPMulticast.this.listenPort);
                datagramSocket.joinGroup(UDPMulticast.this.sendAddress);
                ConnectStatus connectStatus = ConnectStatus.Connected;
                if (UDPMulticast.this.statusListener != null) {
                    UDPMulticast.this.statusListener.onConnectStatusChanged(UDPMulticast.this, ConnectStatus.Connected);
                }

                Log.d(TAG, "UDP 启动接收...");
                byte[]         message        = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(message, message.length);

                try {
                    while (UDPMulticast.this.isRunning) {
                        UDPMulticast.this.lock.acquire();

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (SocketTimeoutException e) {
                            continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] data    = datagramPacket.getData();
                        int    dataLen = datagramPacket.getLength();
                        byte[] dataNew = new byte[dataLen];
                        System.arraycopy(data, 0, dataNew, 0, dataLen);
                        Log.d(TAG, "接收：" + datagramPacket.getAddress().getHostAddress() + ":" + DataConvert.Bytes2HexString(dataNew, true));
                        if (UDPMulticast.this.receiveListener != null) {
                            UDPMulticast.this.receiveListener.onNetReceive(UDPMulticast.this, dataNew);
                        }

                        UDPMulticast.this.lock.release();
                    }

                    Log.e(TAG, "UDP 接收功能结束");
                } catch (Exception e) {
                    e.printStackTrace();
                    connectStatus = ConnectStatus.Error;
                    if (UDPMulticast.this.isRunning && UDPMulticast.this.statusListener != null) {
                        UDPMulticast.this.statusListener.onConnectStatusChanged(UDPMulticast.this, ConnectStatus.Error);
                    }
                }

                datagramSocket.leaveGroup(UDPMulticast.this.sendAddress);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "UDP Close");
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
            }
        }
    }
}
