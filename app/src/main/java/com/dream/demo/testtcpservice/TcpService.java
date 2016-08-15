package com.dream.demo.testtcpservice;

import com.alibaba.fastjson.JSON;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpService extends Thread {
    ServerSocket serverSocket;// 创建ServerSocket对象
    Socket       clicksSocket;// 连接通道，创建Socket对象
    InputStream  inputstream;// 创建输入数据流
    OutputStream outputStream;// 创建输出数据流
    MainActivity activity;
    String type = "";
    private Command mCommand;

    public TcpService(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            int port = 51888;
            serverSocket = new ServerSocket(port);
            mCommand = new Command();
            mCommand.setResolveCommandListener(new ResolveCommandListener() {
                public void resolveCommandHandle(Object sender, byte messageType, String json) {
                    switch (messageType) {
                        case 0x01:
                            IPS ips = new IPS();
                            String ipString = Util.getIP();//socket.getInetAddress().toString();
                            ips.setIp(ipString);
                            sendMsg(Command.create((byte) 0x02,JSON.toJSONString(ips)));
                            break;
                        case 0x03:
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    activity.setData();
                                }
                            });
                            break;
                        case 0x05:

                            break;
                    }

                    //                    activity.runOnUiThread(new Runnable() {
                    //                        public void run() {
                    //                            activity.receiveEditText.setText(new String(buf, 4, len - 1));
                    //                            if (!"".equals(new String(buf, 4, len - 1))) {
                    //                                try {
                    //                                    JSONObject jsonObject = new JSONObject(new String(buf, 4, len - 1));
                    //                                    type = jsonObject.getString("type");
                    //
                    //                                } catch (JSONException e) {
                    //
                    //                                    e.printStackTrace();
                    //                                }
                    //                                if (!"".equals(type) && "request".equals(type)) {
                    //                                    activity.setData();
                    //                                }
                    //                                IPS    ips      = new IPS();
                    //                                String ipString = Util.getIP();//socket.getInetAddress().toString();
                    //                                ips.setIp(ipString);
                    //                                sendMsg(JSON.toJSONString(ips));
                    //                            }
                    //                        }
                    //                    });
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (true) {
            try {

                clicksSocket = serverSocket.accept();
                inputstream = clicksSocket.getInputStream();//
                // 启动接收线程
                Receive_Thread receive_Thread = new Receive_Thread();
                receive_Thread.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 接收线程
     */
    class Receive_Thread extends Thread {
        public void run() {
            while (true) {
                try {
                    final byte[] buf = new byte[1024];
                    final int    len = inputstream.read(buf);

                    if (len == -1) {
                        break;
                    }

                    if (len >= 1) {
                        byte[] data = new byte[len];
                        System.arraycopy(buf, 0, data, 0, len);
                        mCommand.resolve(TcpService.this, data);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMsg(String msg) {
        try {

            outputStream = clicksSocket.getOutputStream();

            outputStream.write(msg.getBytes());
            // outputStream.write("0".getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void sendMsg(byte[] buf) {
        try {

            outputStream = clicksSocket.getOutputStream();

            outputStream.write(buf);
            // outputStream.write("0".getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
