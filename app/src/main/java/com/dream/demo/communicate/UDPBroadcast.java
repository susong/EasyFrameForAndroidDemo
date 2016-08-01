package com.dream.demo.communicate;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午2:11
 * Description: UDP广播
 */
public class UDPBroadcast {

    private WifiManager.MulticastLock lock;
    private int                        listenPort      = 10006;
    private int                        sendPort        = 10007;
    private InetAddress                sendAddress     = null;
    private ReceiveListener            receiveListener = null;
    private ConnectStatusListener      statusListener  = null;
    private UDPBroadcast.ReceiveThread receiveThread   = null;
    private boolean                    isRunning       = false;

    public UDPBroadcast(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.lock = manager.createMulticastLock("UDPwifi");
    }

    public void setInfo(String listenPort, String sendPort) {
        this.listenPort = Integer.valueOf(listenPort.toString());
        this.sendPort = Integer.valueOf(sendPort.toString()).intValue();
    }

    public void SetReceiveListener(ReceiveListener listener) {
        this.receiveListener = listener;
    }

    public void SetStatusListener(ConnectStatusListener listener) {
        this.statusListener = listener;
    }

    public Boolean Start() {
        this.isRunning = true;
        this.receiveThread = new UDPBroadcast.ReceiveThread();
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
        Log.e("UDPBroadcast", "发送：" + DataConvert.Bytes2HexString(cmd, true));
        try {
            //广播地址
            this.sendAddress = InetAddress.getByName("255.255.255.255");
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.send(new DatagramPacket(cmd, cmd.length, this.sendAddress, this.sendPort));
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class ReceiveThread extends Thread {

        public void run() {
            DatagramSocket datagramSocket = null;

            try {
                datagramSocket = new DatagramSocket((SocketAddress) null);
                datagramSocket.setBroadcast(true);
                datagramSocket.setSoTimeout(10000);
                datagramSocket.setReuseAddress(true);
                datagramSocket.bind(new InetSocketAddress(UDPBroadcast.this.listenPort));
                if (UDPBroadcast.this.statusListener != null) {
                    UDPBroadcast.this.statusListener.onConnectStatusChanged(UDPBroadcast.this, ConnectStatus.Connected);
                }

                Log.d("UDPBroadcast", "UDP 启动接收...");
                byte[]         buf            = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

                try {
                    while (UDPBroadcast.this.isRunning) {
                        UDPBroadcast.this.lock.acquire();

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (SocketTimeoutException e) {
                            continue;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            continue;
                        }

                        byte[] e1      = datagramPacket.getData();
                        int    dataLen = datagramPacket.getLength();
                        byte[] data    = new byte[dataLen];
                        System.arraycopy(e1, 0, data, 0, dataLen);
                        Log.e("UDPBroadcast", "接收：" + datagramPacket.getAddress().getHostAddress() + ":" + DataConvert.Bytes2HexString(data, true));
                        UDPBroadcast.this.lock.release();
                        if (UDPBroadcast.this.receiveListener != null) {
                            UDPBroadcast.this.receiveListener.onNetReceive(UDPBroadcast.this, data);
                        }
                    }

                    Log.e("UDPBroadcast", "UDP 接收功能结束");
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

                Log.d("UDPBroadcast", "UDP Close");
            }

        }
    }
}
