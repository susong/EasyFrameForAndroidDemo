package com.dream.demo.net.tcp;

import android.text.TextUtils;

import com.dream.demo.net.interf.ReceiveListener;
import com.dream.demo.net.utils.DataConvert;
import com.dream.library.utils.AbLog;

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

    private static final String TAG = "TcpServer";
    private int             mPort;
    private ReceiveListener mReceiveListener;
    private ServerSocket    mServerSocket;
    private boolean                      mDebug                         = true;
    private boolean                      mIsStop                        = true;
    private Map<String, Socket>          mHostAddressSocketMap          = new HashMap<>();
    private Map<String, ReceiveRunnable> mHostAddressReceiveRunnableMap = new HashMap<>();

    public TcpServer(Builder builder) {
        this.mPort = builder.mPort;
        this.mReceiveListener = builder.mReceiveListener;
    }

    public void start() {
        if (mIsStop) {
            if (mDebug) {
                AbLog.i(TAG, "TcpServer 开启 端口:" + mPort);
            }
            mIsStop = false;
            new Thread(new ServerRunnable()).start();
        } else {
            if (mDebug) {
                AbLog.e(TAG, "TcpServer 已经是开启的了");
            }
        }
    }

    public void stop() {
        if (mDebug) {
            AbLog.i(TAG, "TcpServer 停止");
        }
        mIsStop = true;

        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException ignored) {
            }
        }
        if (mHostAddressReceiveRunnableMap != null) {
            for (Map.Entry<String, ReceiveRunnable> entry : mHostAddressReceiveRunnableMap.entrySet()) {
                ReceiveRunnable runnable = entry.getValue();
                runnable.stop();
            }
            mHostAddressReceiveRunnableMap.clear();
        }
    }

    public void send(String msg) {
        send(null, msg);
    }

    public void send(String hostAddress, String msg) {
        try {
            if (mDebug) {
                AbLog.i(TAG, "TcpServer 发送 " + "端口：" + mPort + " 数据：" + msg);
            }
            send(hostAddress, msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] cmd) {
        send(null, cmd);
    }

    public void send(String hostAddress, byte[] cmd) {
        if (cmd == null || cmd.length < 1 || mHostAddressSocketMap.size() == 0) {
            if (mDebug) {
                AbLog.e(TAG, "TcpServer 没有客户端接入或数据有问题");
            }
            return;
        }

        if (mDebug) {
            AbLog.i(TAG, "TcpServer 发送：" + DataConvert.Bytes2HexString(cmd, true));
        }

        try {
            if (TextUtils.isEmpty(hostAddress)) {
                for (Map.Entry<String, Socket> entry : mHostAddressSocketMap.entrySet()) {
                    Socket       socket = entry.getValue();
                    OutputStream out    = socket.getOutputStream();
                    out.write(cmd);
                    out.flush();
                }
            } else {
                Socket       socket = mHostAddressSocketMap.get(hostAddress);
                OutputStream out    = socket.getOutputStream();
                out.write(cmd);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                if (mDebug) {
                    AbLog.i(TAG, "启动服务 端口:" + mPort);
                }
                mServerSocket = new ServerSocket(mPort);
                while (!mIsStop) {
                    try {
                        Socket acceptSocket = mServerSocket.accept();
                        if (mDebug) {
                            AbLog.i(TAG, "客户端接入 ip:" + acceptSocket.getInetAddress().getHostAddress() + " 端口：" + mPort);
                        }
                        ReceiveRunnable receiveRunnable = new ReceiveRunnable(acceptSocket);
                        new Thread(receiveRunnable).start();

                        mHostAddressSocketMap.put(acceptSocket.getInetAddress().getHostAddress(), acceptSocket);
                        mHostAddressReceiveRunnableMap.put(acceptSocket.getInetAddress().getHostAddress(), receiveRunnable);
                    } catch (IOException e) {
                        if (!mIsStop) {
                            AbLog.e(TAG, e.getMessage());
                        }
                    }
                }
                if (mDebug) {
                    AbLog.i(TAG, "退出服务 端口:" + mPort);
                }
                mHostAddressSocketMap.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveRunnable implements Runnable {

        private String mLocalAddress;//本地地址，服务端地址
        private String mInetAddress;//远程地址，客户端地址
        private Socket mSocket;
        private InputStream mIn = null;

        public ReceiveRunnable(Socket socket) {
            this.mSocket = socket;
            this.mLocalAddress = socket.getLocalAddress().getHostAddress();
            this.mInetAddress = socket.getInetAddress().getHostAddress();
            try {
                this.mIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException ignored) {
                }
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int    length;

            if (mDebug) {
                AbLog.i(TAG, mInetAddress + ":" + mPort + " 开始接收...");
            }

            while (!mIsStop) {
                try {
                    // 阻塞线程
                    length = mIn.read(buffer);
                } catch (IOException e) {
                    if (!mIsStop) {
                        AbLog.e(TAG, e.getMessage());
                    }
                    break;
                }

                if (length == -1) {//连接中断
                    if (mDebug) {
                        AbLog.i(TAG, mInetAddress + ":" + mPort + " 连接中断");
                    }
                    break;
                }

                if (length < 1) {
                    continue;
                }

                byte[] data = new byte[length];
                System.arraycopy(buffer, 0, data, 0, length);

                if (mDebug) {
                    AbLog.i(TAG, mInetAddress + ":" + mPort + " 接收到：" + DataConvert.Bytes2HexString(data, true));
                    try {
                        AbLog.i(TAG, mInetAddress + ":" + mPort + " 接收到：" + new String(data, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    mReceiveListener.onNetReceive(mInetAddress, data);
                } catch (Exception e) {
                    AbLog.e(TAG, "", e);
                }
            }

            if (mDebug) {
                AbLog.i(TAG, mInetAddress + ":" + mPort + " 接收线程退出");
            }
        }
    }

    public static class Builder {
        private int             mPort            = 8888;
        private ReceiveListener mReceiveListener = null;

        public Builder port(Object port) {
            try {
                this.mPort = Integer.valueOf(String.valueOf(port));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder receiverListener(ReceiveListener receiveListener) {
            this.mReceiveListener = receiveListener;
            return this;
        }

        public TcpServer build() {
            return new TcpServer(this);
        }
    }
}
