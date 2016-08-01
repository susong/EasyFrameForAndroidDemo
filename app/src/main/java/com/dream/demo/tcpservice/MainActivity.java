package com.dream.demo.tcpservice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.dream.demo.R;

public class MainActivity extends Activity {
    Button startButton;// 发送按钮

    EditText receiveEditText;// 接收消息框
    Button   sendButton;// 发送按钮
    Button   sendButton1;// 发送按钮


    TcpService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start_button);
        sendButton1 = (Button) findViewById(R.id.send_button1);
        receiveEditText = (EditText) findViewById(R.id.receive_EditText);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton1.setOnClickListener(startButton1Listener);
        startButton.setOnClickListener(startButtonListener);
        sendButton.setOnClickListener(sendButtonListener);
        UdpBroadcastUtil.registerBroadcastReceiver(MainActivity.this);
        sendUDP();
        service = new TcpService(MainActivity.this);
        service.start();
    }

    /**
     *
     */
    private OnClickListener startButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            setData();
        }
    };

    /**
     *
     */
    private OnClickListener startButton1Listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            sendUDP();
        }


    };

    private void sendUDP() {
        String ips  = Util.getIP();
        IPS    ips2 = new IPS();
        ips2.setIp(ips);
        String jsonString = JSON.toJSONString(ips2);
        UdpBroadcastUtil.sendBroadcast(jsonString);
    }

    private OnClickListener sendButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String ips3 = Util.getIP();
            IPS    ips4 = new IPS();
            ips4.setIp(ips3);
            String jsonString4 = JSON.toJSONString(ips4);
            service.sendMsg(jsonString4);
        }
    };

    public void setData() {
        String     ip   = Util.getIP();
        DeviceList list = new DeviceList();
        list.setIp(ip);
        list.setMac("c893464046");
        list.setName("客厅电视");
        list.setRegion("客厅");
        list.setType("2");

        DeviceList list1 = new DeviceList();
        list1.setIp(ip);
        list1.setMac("c893464046");
        list1.setName("客厅空调");
        list1.setRegion("客厅");
        list1.setType("5");

        DeviceList list2 = new DeviceList();
        list2.setIp(ip);
        list2.setMac("c893464046");
        list2.setName("客厅顶灯");
        list2.setRegion("客厅");
        list2.setType("1");


        DevideData.DataToShare dataToShare = new DevideData.DataToShare();
        dataToShare.Devices.add(list);
        dataToShare.Devices.add(list1);
        dataToShare.Devices.add(list2);
        String jsonString1 = JSON.toJSONString(dataToShare);
        Log.d("XLog", jsonString1);
        service.sendMsg(Command.create((byte) 0x04, jsonString1));
    }
}
