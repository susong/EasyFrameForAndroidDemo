package com.dream.demo.tcpservice;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 下午3:34
 * Description: ApplianceServer
 */
public interface ResolveCommandListener {
    void resolveCommandHandle(Object sender, byte messageType, String json);
}
