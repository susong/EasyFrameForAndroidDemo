package com.dream.demo.net;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/6/27 上午10:44
 * Description: ApplianceServer
 */
public class DataConvert {
    private static String hexString = "0123456789ABCDEF";

    public DataConvert() {
    }

    public static String getHexString(byte[] bytes) {
        char[] chs = getChars(bytes);
        String res = new String(chs);
        res = decode(res);
        return res.toUpperCase();
    }

    public static byte[] getBytes(char[] chars) {
        Charset    cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static char[] getChars(byte[] bytes) {
        return getChars(bytes, "ISO-8859-1");
    }

    public static char[] getChars(byte[] bytes, String charsetName) {
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        Charset    cs = Charset.forName(charsetName);
        CharBuffer cb = cs.decode(bb);
        cb.flip();
        return cb.array();
    }

    public static String stringToHexString(String strPart) {
        String hexString = "";

        for (int i = 0; i < strPart.length(); ++i) {
            char   ch     = strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            if (strHex.length() < 2) {
                strHex = "0" + strHex;
            }

            hexString = hexString + strHex.toUpperCase();
        }

        return hexString;
    }

    public static String encode(String str) {
        byte[]        bytes = str.getBytes();
        StringBuilder sb    = new StringBuilder(bytes.length * 2);
        byte[]        var3  = bytes;
        int           var4  = bytes.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            byte aByte = var3[var5];
            sb.append(hexString.charAt((aByte & 240) >> 4));
            sb.append(hexString.charAt(aByte & 15));
        }

        return sb.toString();
    }

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);

        for (int i = 0; i < bytes.length(); i += 2) {
            baos.write(hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1)));
        }

        return new String(baos.toByteArray());
    }

    public static byte[] intToByte(int number) {
        int    temp = number;
        byte[] b    = new byte[4];

        for (int i = 0; i < b.length; ++i) {
            b[i] = Integer.valueOf(temp & 255).byteValue();
            temp >>= 8;
        }

        return b;
    }

    public static int bytesToInt(byte[] b) {
        byte[] a = new byte[4];
        int    i = a.length - 1;

        for (int j = b.length - 1; i >= 0; --j) {
            if (j >= 0) {
                a[i] = b[j];
            } else {
                a[i] = 0;
            }

            --i;
        }

        int v0 = (a[0] & 255) << 24;
        int v1 = (a[1] & 255) << 16;
        int v2 = (a[2] & 255) << 8;
        int v3 = a[3] & 255;
        return v0 + v1 + v2 + v3;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}));
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}));
        return (byte) (_b0 ^ _b1);
    }

    public static byte[] HexString2Bytes(String src) {
        src = src.replace(" ", "");
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < tmp.length / 2; ++i) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static void printHexString(String hint, byte[] buf) {
        System.out.print(hint);
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase() + " ");
        }
        System.out.println("");
    }

    public static String Bytes2HexString(byte[] b, boolean isSeparate) {
        return Bytes2HexString(b, 0, b.length, isSeparate);
    }

    public static String Bytes2HexString(byte[] b, int size, boolean isSeparate) {
        return Bytes2HexString(b, 0, size, isSeparate);
    }

    public static String Bytes2HexString(byte[] b, int start, int length, boolean isSeparate) {
        StringBuilder ret = new StringBuilder();

        for (int i = start; i < start + length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            ret.append(hex.toUpperCase());
            if (isSeparate) {
                ret.append(" ");
            }
        }

        return ret.toString();
    }

    public static int getUnsignedInt(int signedByte) {
        return signedByte >= 0 ? signedByte : signedByte + 256;
    }

    public static byte getSignedByte(int bigInt) {
        int  temp = bigInt % 256;
        byte byteValue;
        if (bigInt < 0) {
            byteValue = (byte) (temp < -128 ? 256 + temp : temp);
        } else {
            byteValue = (byte) (temp > 127 ? temp - 256 : temp);
        }

        return byteValue;
    }
}
