package com.dream.demo.net.tcp;

import android.os.SystemClock;
import android.util.Log;

import com.dream.demo.net.ConnectStatus;
import com.dream.demo.net.ConnectStatusListener;
import com.dream.demo.net.DataConvert;
import com.dream.demo.net.ReceiveListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 会保持长连接，中间断开后也会自动重连，直到调用stop()为止
 */
public class TcpClient implements Communication_Net {

    private static final boolean               DEBUG           = true;
    private static final String                tag             = "TcpClient";
    private              String                host            = "127.0.0.1";
    private              int                   port            = 0;
    private              Socket                socket          = null;
    private              InputStream           in              = null;
    private              OutputStream          out             = null;
    private              ReceiveListener       receiveListener = null;
    private              ConnectStatusListener statusListener  = null;
    private              boolean               isStop          = true;
    /**
     * 0-未连接 1-连接中 2-已连接
     */
    private              int                   isConnected     = 0;
    private ConnectStatus currentStatus;
    private String        error;

    @Override
    public void SetInfo(Object host, Object port) {
        host = String.valueOf(host);

        try {
            port = Integer.valueOf(String.valueOf(port));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void SetReceiveListener(ReceiveListener callback) {
        this.receiveListener = callback;
    }

    @Override
    public void Start() {
        isStop = false;

        ClientThread clientThread = new ClientThread();
        clientThread.start();
    }

    @Override
    public void Stop() {
        Log.e(tag, "TCP stop");

        isStop = true;
        isConnected = 0;

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }

        onConnectStatusChanged(ConnectStatus.DisConnected, "TcpClient stop");
    }

    @Override
    public void Write(byte[] cmd) {
        if (isConnected < 2 || cmd == null || cmd.length < 1 || out == null) {
            return;
        }

        if (DEBUG) {
            Log.i(tag, "发送：" + DataConvert.Bytes2HexString(cmd, true));
        }

        try {
            out.write(cmd);
            out.flush();
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
            onConnectStatusChanged(ConnectStatus.Error, e.getMessage());
        }
    }

    @Override
    public void SetStatusListener(ConnectStatusListener listener) {
        this.statusListener = listener;
    }

    @Override
    public ConnectStatus getStatus() {
        return currentStatus;
    }

    @Override
    public String getError() {
        return error;
    }

    private void onConnectStatusChanged(ConnectStatus status, String error) {
        currentStatus = status;
        this.error = error;

        if (statusListener != null) {
            statusListener.onConnectStatusChanged(this, currentStatus);
        }

        if (status == ConnectStatus.Error) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }

            isConnected = 0;

            if (!isStop) {
                SystemClock.sleep(1000);

                ClientThread clientThread = new ClientThread();
                clientThread.start();
            }
        }
    }

    class ClientThread extends Thread {
        @Override
        public void run() {
            if (isStop || isConnected > 0)
                return;//已停止 或 正在连接 或 已连接
            isConnected = 1;
            Log.i(tag, "开始连接：" + host + ":" + port);

            try {
                socket = new Socket();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.connect(new InetSocketAddress(host, port), 3000);

                in = socket.getInputStream();
                out = socket.getOutputStream();
                isConnected = 2;
            } catch (IOException e) {
                Log.e(tag, e.getMessage());
                onConnectStatusChanged(ConnectStatus.Error, e.getMessage());
            }

            if (isConnected == 2) {
                Log.i(tag, "连接成功！");
                onConnectStatusChanged(ConnectStatus.Connected, "连接成功");

                ReceiveThread thread = new ReceiveThread();
                thread.start();
            }

            Log.i(tag, host + ":" + port + " 连接线程退出");
        }
    }

    class ReceiveThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int    length;

            if (DEBUG)
                Log.i(tag, "开始接收...");

            while (!isStop) {
                try {
                    length = in.read(buffer);
                } catch (IOException e) {
                    if (!isStop) {
                        Log.e(tag, e.getMessage());
                        onConnectStatusChanged(ConnectStatus.Error, "连接中断");
                    }
                    break;
                }

                if (length == -1)//连接中断
                    break;

                if (length < 1)
                    continue;

                byte[] data = new byte[length];
                System.arraycopy(buffer, 0, data, 0, length);

                if (DEBUG)
                    Log.i(tag, "接收到：" + DataConvert.Bytes2HexString(data, true));

                try {
                    receiveListener.onNetReceive(TcpClient.this, data);
                } catch (Exception ex) {
                    Log.e(tag, "", ex);
                }
            }

            Log.i(tag, host + ":" + port + " 接收线程退出");
        }
    }
}
