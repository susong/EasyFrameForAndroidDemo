package com.dream.demo.testtcpservice;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午3:29
 * Description: 指令生成和解析类 协议头(0xAA) 长度(0xXXXX) 指令编号(0xXX) 数据 校验(0xXX)
 */
public class Command {

    private static final String  TAG   = "XLog_Command";
    private static final boolean DEBUG = true;
    private ResolveCommandListener resolveCommandListener;
    private Hashtable<Object, ArrayList<Byte>> dictionary = new Hashtable<Object, ArrayList<Byte>>();

    public void setResolveCommandListener(ResolveCommandListener resolveCommandListener) {
        this.resolveCommandListener = resolveCommandListener;
    }

    /**
     * 协议的合成
     */
    public static byte[] create(byte messageType, String message) {
        byte[] data = null;
        try {
            data = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] command = null;
        if (data != null) {
            command = create(messageType, data, data.length);
        }

        if (DEBUG)
            Log.d(TAG, String.format("Create Net指令: 0x%02X %s", messageType, message));

        return command;
    }

    /**
     * 协议的合成
     */
    private static byte[] create(byte messageType, byte[] data, int len) {

        // 指令长度
        byte[] command = new byte[5 + len];

        // 协议头，一个字节，为固定的0xAA。
        command[0] = (byte) 0xAA;

        // 长度，两个字节，为协议中“数据”部分的长度，不包含指令编号和校验。
        command[1] = (byte) (data.length >> 8);
        command[2] = (byte) data.length;

        // 指令编号，一个字节，参见指令分配规则。
        command[3] = messageType;

        // json数据部分
        System.arraycopy(data, 0, command, 4, len);

        // 校验
        int fcs = 0;
        for (int i = 1, size = command.length; i < size; ++i) {
            fcs = fcs ^ command[i];
        }
        command[command.length - 1] = (byte) fcs;
        return command;
    }


    /**
     * 解析接收到的数据
     */
    public void resolve(Object sender, byte[] data) {

        if (!dictionary.containsKey(sender)) {
            dictionary.put(sender, new ArrayList<Byte>());
        }
        String string = new String(data);
        List<Byte> messageList = dictionary.get(sender);

        for (byte b : data) {
            messageList.add(b);
        }

        while (true) {
            try {
                // 协议头
                Byte head = (byte) 0xAA;
                // 协议头索引
                int headIndex = messageList.indexOf(head);
                if (headIndex < 0) {
                    return;// 没找到协议头，则返回
                }
                // 移除前面的无效数据
                for (int i = 0; i < headIndex; ++i) {
                    messageList.remove(0);
                }

                int size = messageList.size();
                if (size < 3) {
                    return;// 长度不够
                }

                // 数据长度
                byte[] dataLengthByte = new byte[]{messageList.get(1), messageList.get(2)};
                int    dataLength     = DataConvert.bytesToInt(dataLengthByte);
                int    length         = 5 + dataLength;
                if (size < length) {
                    return;// 长度不够
                }

                // 协议校验
                int  end       = length - 1;
                byte fcs       = messageList.get(end);
                int  xorResult = 0;
                for (int i = 1; i < end; ++i) {
                    xorResult = xorResult ^ messageList.get(i);
                }
                if (fcs != xorResult) {
                    messageList.remove(0);//校验不通过
                    continue;
                }

                // 触发接收到协议事件
                if (resolveCommandListener != null) {
                    byte messageType = messageList.get(3);

                    byte[] dataByte = new byte[dataLength];
                    for (int i = 0; i < dataLength; i++) {
                        dataByte[i] = messageList.get(4 + i);
                    }

                    String jsonString = new String(dataByte, "UTF-8");

                    if (DEBUG)
                        Log.d(TAG, String.format("Resolve Net指令: 0x%02X %s", messageType, jsonString));

                    resolveCommandListener.resolveCommandHandle(sender, messageType, jsonString);
                }

                // 删除识别出的协议
                for (int i = 0; i < length; i++) {
                    messageList.remove(0);
                }
            } catch (Exception e) {
                Byte[] messageByte = new Byte[messageList.size()];
                messageList.toArray(messageByte);
                byte[] messageByteNew = new byte[messageByte.length];
                for (int i = 0; i < messageByte.length; ++i) {
                    messageByteNew[i] = messageByte[i];
                }
                messageList.clear();
                Log.e(TAG, "无法解析的指令：" + DataConvert.Bytes2HexString(messageByteNew, true));
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dictionary.clear();
    }
}
