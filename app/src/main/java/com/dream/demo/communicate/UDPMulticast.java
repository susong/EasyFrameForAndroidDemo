package com.dream.demo.communicate;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午2:11
 * Description: UDP多播
 */
public class UDPMulticast {

    private static final String TAG = "UDPMulticast";
    private WifiManager.MulticastLock lock;
    private int                        listenPort      = 10006;
    private int                        sendPort        = 10007;
    private InetAddress                group           = null;
    private ReceiveListener            receiveListener = null;
    private ConnectStatusListener      statusListener  = null;
    private UDPMulticast.ReceiveThread receiveThread   = null;
    private boolean                    isRunning       = false;

    public UDPMulticast(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.lock = manager.createMulticastLock("UDPwifi");
    }

    public void SetInfo(Object arg0, Object arg1, Object arg2) {
        this.listenPort = Integer.valueOf(arg0.toString()).intValue();
        this.sendPort = Integer.valueOf(arg1.toString()).intValue();

        try {
            this.group = InetAddress.getByName("224.0.0.1");
        } catch (UnknownHostException var5) {
            var5.printStackTrace();
        }

    }

    public void SetReceiveListener(ReceiveListener listener) {
        this.receiveListener = listener;
    }

    public void SetStatusListener(ConnectStatusListener listener) {
        this.statusListener = listener;
    }

    public Boolean Start() {
        this.isRunning = true;
        this.receiveThread = new UDPMulticast.ReceiveThread();
        this.receiveThread.start();
        return null;
    }

    public void Stop() {
        this.isRunning = false;
        if (this.receiveThread != null) {
            this.receiveThread.interrupt();
        }
    }

    public void Write(byte[] cmd) {
        Log.e("UDPMulticast", "发送：" + DataConvert.Bytes2HexString(cmd, true));

        try {
            MulticastSocket e = new MulticastSocket(this.sendPort);
            e.joinGroup(this.group);
            e.setTimeToLive(5);
            DatagramPacket packet = new DatagramPacket(cmd, cmd.length, this.group, this.sendPort);
            e.send(packet);
            e.leaveGroup(this.group);
            e.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    class ReceiveThread extends Thread {
        ReceiveThread() {
        }

        public void run() {
            MulticastSocket datagramSocket = null;

            try {
                datagramSocket = new MulticastSocket(UDPMulticast.this.listenPort);
                datagramSocket.joinGroup(UDPMulticast.this.group);
                ConnectStatus e = ConnectStatus.Connected;
                if (UDPMulticast.this.statusListener != null) {
                    UDPMulticast.this.statusListener.onConnectStatusChanged(UDPMulticast.this, ConnectStatus.Connected);
                }

                Log.d("UDPMulticast", "UDP 启动接收...");
                byte[]         message        = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(message, message.length);

                try {
                    while (UDPMulticast.this.isRunning) {
                        UDPMulticast.this.lock.acquire();

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (SocketTimeoutException var14) {
                            continue;
                        } catch (IOException var15) {
                            var15.printStackTrace();
                        }

                        byte[] e1      = datagramPacket.getData();
                        int    dataLen = datagramPacket.getLength();
                        byte[] data    = new byte[dataLen];
                        System.arraycopy(e1, 0, data, 0, dataLen);
                        Log.d("UDPMulticast", "接收：" + datagramPacket.getAddress().getHostAddress() + ":" + DataConvert.Bytes2HexString(data, true));
                        if (UDPMulticast.this.receiveListener != null) {
                            UDPMulticast.this.receiveListener.onNetReceive(UDPMulticast.this, data);
                        }

                        UDPMulticast.this.lock.release();
                    }

                    Log.e("UDPMulticast", "UDP 接收功能结束");
                } catch (Exception var16) {
                    var16.printStackTrace();
                    e = ConnectStatus.Error;
                    if (UDPMulticast.this.isRunning && UDPMulticast.this.statusListener != null) {
                        UDPMulticast.this.statusListener.onConnectStatusChanged(UDPMulticast.this, ConnectStatus.Error);
                    }
                }

                datagramSocket.leaveGroup(UDPMulticast.this.group);
            } catch (Exception var17) {
                var17.printStackTrace();
            } finally {
                Log.d("UDPMulticast", "UDP Close");
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
            }
        }
    }
}
