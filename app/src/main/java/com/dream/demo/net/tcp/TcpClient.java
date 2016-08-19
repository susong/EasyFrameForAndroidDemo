package com.dream.demo.net.tcp;

import android.os.SystemClock;
import android.util.Log;

import com.dream.demo.net.interf.ConnectStatus;
import com.dream.demo.net.interf.ConnectStatusListener;
import com.dream.demo.net.utils.DataConvert;
import com.dream.demo.net.interf.ReceiveListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/8/15 下午3:32
 * Description: EasyFrameForAndroidDemo
 * 会保持长连接，中间断开后也会自动重连，直到调用stop()为止
 */
public class TcpClient {

    private static final boolean               DEBUG            = true;
    private static final String                TAG              = "TcpClient";
    private              String                mHost            = "127.0.0.1";
    private              int                   mPort            = 8888;
    private              Socket                mSocket          = null;
    private              InputStream           mIn              = null;
    private              OutputStream          mOut             = null;
    private              ReceiveListener       mReceiveListener = null;
    private              ConnectStatusListener mStatusListener  = null;
    private              boolean               mIsStop          = true;
    private              ConnectStatus         mConnectStatus   = null;
    private              String                mError           = null;
    /** 0-未连接 1-连接中 2-已连接 */
    private              int                   mIsConnected     = 0;

    public void setInfo(Object host, Object port) {
        this.mHost = String.valueOf(host);
        try {
            this.mPort = Integer.valueOf(String.valueOf(port));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void setReceiveListener(ReceiveListener callback) {
        this.mReceiveListener = callback;
    }

    public void start() {
        if (DEBUG) {
            Log.e(TAG, "TcpClient 开始 IP:" + mHost + " 端口:" + mPort);
        }
        mIsStop = false;
        ClientThread clientThread = new ClientThread();
        clientThread.start();
    }

    public void stop() {
        if (DEBUG) {
            Log.e(TAG, "TcpClient stop");
        }
        mIsStop = true;
        mIsConnected = 0;

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException ignored) {
            }
        }

        onConnectStatusChanged(ConnectStatus.DisConnected, "TcpClient stop");
    }

    public void send(String msg) {
        try {
            if (DEBUG) {
                Log.d(TAG, "TcpClient 发送 端口:" + mPort + " 数据:" + msg);
            }
            send(msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] cmd) {
        if (mIsConnected < 2 || cmd == null || cmd.length < 1 || mOut == null) {
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "TcpClient 发送 端口:" + mPort + " 数据:" + DataConvert.Bytes2HexString(cmd, true));
        }

        try {
            mOut.write(cmd);
            mOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            onConnectStatusChanged(ConnectStatus.Error, e.getMessage());
        }
    }

    public void setStatusListener(ConnectStatusListener listener) {
        this.mStatusListener = listener;
    }

    public ConnectStatus getStatus() {
        return mConnectStatus;
    }

    public String getError() {
        return mError;
    }

    private void onConnectStatusChanged(ConnectStatus status, String error) {
        mConnectStatus = status;
        this.mError = error;

        if (mStatusListener != null) {
            mStatusListener.onConnectStatusChanged(this, mConnectStatus);
        }

        if (status == ConnectStatus.Error) {
            try {
                mSocket.close();
            } catch (IOException ignored) {
            }

            mIsConnected = 0;

            if (!mIsStop) {
                SystemClock.sleep(1000);

                ClientThread clientThread = new ClientThread();
                clientThread.start();
            }
        }
    }

    private class ClientThread extends Thread {

        @Override
        public void run() {
            if (mIsStop || mIsConnected > 0) {
                return;//已停止 或 正在连接 或 已连接
            }
            mIsConnected = 1;
            if (DEBUG) {
                Log.i(TAG, "开始连接 IP:" + mHost + " 端口:" + mPort);
            }

            try {
                mSocket = new Socket();
                mSocket.setTcpNoDelay(true);
                mSocket.setKeepAlive(true);
                mSocket.connect(new InetSocketAddress(mHost, mPort), 3000);

                //mIn = mSocket.getInputStream();
                //mOut = mSocket.getOutputStream();
                mIsConnected = 2;

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                onConnectStatusChanged(ConnectStatus.Error, e.getMessage());
            }

            if (mIsConnected == 2) {

                if (DEBUG) {
                    Log.i(TAG, "连接成功");
                }
                onConnectStatusChanged(ConnectStatus.Connected, "连接成功");

                new Thread(new ReceiveThread(mSocket)).start();
            }

            if (DEBUG) {
                Log.i(TAG, mHost + ":" + mPort + " 连接线程退出");
            }
        }
    }

    private class ReceiveThread implements Runnable {

        private String mLocalAddress;//本地地址，客户端地址
        private String mInetAddress;//远程地址，服务端地址
        private Socket mSocket;

        public ReceiveThread(Socket socket) {
            this.mSocket = socket;
            this.mLocalAddress = socket.getLocalAddress().getHostAddress();
            this.mInetAddress = socket.getInetAddress().getHostAddress();
            try {
                mIn = socket.getInputStream();
                mOut = socket.getOutputStream();
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
                        onConnectStatusChanged(ConnectStatus.Error, "连接中断");
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
                    mReceiveListener.onNetReceive(TcpClient.this, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (DEBUG) {
                Log.i(TAG, mInetAddress + ":" + mPort + " 接收线程退出");
            }
        }
    }
}
