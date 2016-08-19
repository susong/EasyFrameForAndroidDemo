package com.dream.demo.net.udp;

import android.util.Log;

import com.dream.demo.net.utils.DataConvert;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午2:11
 * Description: UDP多播发送与接收
 */
public class UDPMulticastSend {

    private static final String      TAG          = "UDPMulticast";
    private              int         mSendPort    = 10007;
    private              InetAddress mSendAddress = null;

    public void setSendPort(int sendPort) {
        this.mSendPort = sendPort;
    }

    public void send(String msg) {
        try {
            Log.d(TAG, "UDP多播发送：" + msg);
            send(msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "UDP多播发送：" + DataConvert.Bytes2HexString(cmd, true));
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
}
