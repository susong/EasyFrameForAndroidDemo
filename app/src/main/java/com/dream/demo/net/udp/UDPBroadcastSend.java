package com.dream.demo.net.udp;

import android.util.Log;

import com.dream.demo.net.DataConvert;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午2:11
 * Description: UDP广播发送与接收
 */
public class UDPBroadcastSend {

    private static final String      TAG          = "UDPBroadcast";
    private              int         mSendPort    = 10007;
    private              InetAddress mSendAddress = null;

    public void setSendPort(int sendPort) {
        this.mSendPort = sendPort;
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
}
