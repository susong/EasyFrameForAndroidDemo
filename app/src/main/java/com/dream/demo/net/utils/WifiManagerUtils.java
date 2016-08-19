package com.dream.demo.net.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;


/**
 * wifi管理类
 * author True Lies
 * Created by Administrator on 2016/3/31.
 */
@SuppressWarnings("unused")
public class WifiManagerUtils {

    /**
     * 获取WIFI下ip地址
     */
    public static String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo    wifiInfo    = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format(Locale.CHINA, "%d.%d.%d.%d",
                             (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                             (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public static String getLocalIpAddress() {
        try {
            String                        ip;
            StringBuilder                 ipStringBuilder             = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface         networkInterface       = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&
                        !inetAddress.isLinkLocalAddress() &&
                        inetAddress.isSiteLocalAddress()) {
                        ipStringBuilder.append(inetAddress.getHostAddress());
                    }
                }
            }
            ip = ipStringBuilder.toString();
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBroadcastAddress(Context context) {
        try {
            String              currentBroadcastIp = null;
            ConnectivityManager manager            = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo         info               = manager.getActiveNetworkInfo();
            int                 network_type       = info.getType();
            if (network_type == 1) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                DhcpInfo    dhcp        = wifiManager.getDhcpInfo();
                int         broadcast   = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
                byte[]      quads       = new byte[4];
                for (int k = 0; k < 4; k++) {
                    quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
                    currentBroadcastIp = InetAddress.getByAddress(quads).getHostAddress();
                }
            }
            return currentBroadcastIp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBroadcastAddress2(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo    dhcp = wifi.getDhcpInfo();
            if (dhcp == null) {
                return InetAddress.getByName("255.255.255.255").getHostAddress();
            }
            int    broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads     = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            return InetAddress.getByAddress(quads).getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


