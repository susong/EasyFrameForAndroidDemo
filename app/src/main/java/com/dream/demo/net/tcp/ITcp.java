package com.dream.demo.net.tcp;


import com.dream.demo.net.interf.ConnectStatus;
import com.dream.demo.net.interf.ConnectStatusListener;
import com.dream.demo.net.interf.ReceiveListener;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 上午10:41
 * Description: ApplianceServer
 */
public interface ITcp {

    void setInfo(Object host, Object port);

    void setReceiveListener(ReceiveListener receiveListener);

    void setStatusListener(ConnectStatusListener connectStatusListener);

    void start();

    void stop();

    void send(byte[] cmd);

    ConnectStatus getStatus();

    String getError();
}
