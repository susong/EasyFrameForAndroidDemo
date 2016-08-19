package com.dream.demo.net.tcp;

import android.util.Log;

import com.dream.demo.net.utils.DataConvert;
import com.dream.demo.net.interf.ReceiveListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/8/15 下午3:32
 * Description: EasyFrameForAndroidDemo
 */
public class TcpServer {

    private static final boolean             DEBUG            = true;
    private static final String              TAG              = "TcpServer";
    private              int                 mPort            = 8888;
    private              boolean             mIsStop          = true;
    private              ServerSocket        mServerSocket    = null;
    private              ReceiveListener     mReceiveListener = null;
    private              Map<String, Socket> mStringSocketMap = new HashMap<>();

    public void setPort(Object port) {
        try {
            this.mPort = Integer.valueOf(String.valueOf(port));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void setReceiveListener(ReceiveListener receiveListener) {
        this.mReceiveListener = receiveListener;
    }

    public void start() {
        if (DEBUG) {
            Log.e(TAG, "TcpServer start");
        }
        mIsStop = false;
        new Thread(new ServerThread()).start();
    }

    public void stop() {
        if (DEBUG) {
            Log.e(TAG, "TcpServer stop");
        }
        mIsStop = true;

        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void send(String msg) {
        try {
            if (DEBUG) {
                Log.d(TAG, "TcpServer发送 " + "端口：" + mPort + " 数据：" + msg);
            }
            send(msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] cmd) {
        if (cmd == null || cmd.length < 1 || mStringSocketMap.size() == 0) {
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "发送：" + DataConvert.Bytes2HexString(cmd, true));
        }

        try {
            for (Map.Entry<String, Socket> entry : mStringSocketMap.entrySet()) {
                Socket       socket = entry.getValue();
                OutputStream out    = socket.getOutputStream();
                out.write(cmd);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ServerThread implements Runnable {

        @Override
        public void run() {
            try {
                if (DEBUG) {
                    Log.i(TAG, "启动服务 端口:" + mPort);
                }
                mServerSocket = new ServerSocket(mPort);
                while (!mIsStop) {
                    Socket accept = mServerSocket.accept();
                    if (DEBUG) {
                        Log.i(TAG, "客户端接入 ip:" + accept.getInetAddress().getHostAddress());
                    }
                    mStringSocketMap.put(accept.getInetAddress().getHostAddress(), accept);
                    new Thread(new ReceiveThread(accept)).start();
                }
                if (DEBUG) {
                    Log.i(TAG, "退出服务 端口:" + mPort);
                }
                mStringSocketMap.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveThread implements Runnable {

        private String mLocalAddress;//本地地址，服务端地址
        private String mInetAddress;//远程地址，客户端地址
        private Socket mSocket;
        private InputStream mIn = null;

        public ReceiveThread(Socket socket) {
            this.mSocket = socket;
            this.mLocalAddress = socket.getLocalAddress().getHostAddress();
            this.mInetAddress = socket.getInetAddress().getHostAddress();
            try {
                this.mIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int    length;

            if (DEBUG) {
                Log.i(TAG, "开始接收...");
            }

            while (!mIsStop) {
                try {
                    length = mIn.read(buffer);
                } catch (IOException e) {
                    if (!mIsStop) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }

                if (length == -1) {//连接中断
                    break;
                }

                if (length < 1) {
                    continue;
                }

                byte[] data = new byte[length];
                System.arraycopy(buffer, 0, data, 0, length);

                if (DEBUG) {
                    Log.i(TAG, "接收到：" + DataConvert.Bytes2HexString(data, true));
                }

                try {
                    mReceiveListener.onNetReceive(mInetAddress, data);
                } catch (Exception ex) {
                    Log.e(TAG, "", ex);
                }
            }

            if (DEBUG) {
                Log.i(TAG, mInetAddress + ":" + mPort + " 接收线程退出");
            }
        }
    }
}
