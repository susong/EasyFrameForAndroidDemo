package com.dream.demo.net.tcp;


import com.dream.demo.net.ConnectStatus;
import com.dream.demo.net.ConnectStatusListener;
import com.dream.demo.net.ReceiveListener;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 上午10:41
 * Description: ApplianceServer
 */
public interface Communication_Net {

    void SetInfo(Object host, Object port);

    void SetReceiveListener(ReceiveListener receiveListener);

    void SetStatusListener(ConnectStatusListener connectStatusListener);

    void Start();

    void Stop();

    void Write(byte[] cmd);

    ConnectStatus getStatus();

    String getError();
}
