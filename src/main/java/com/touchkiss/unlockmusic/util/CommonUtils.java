package com.touchkiss.unlockmusic.util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created on 2020/03/17 10:07
 *
 * @author Touchkiss
 */
public class CommonUtils {
    public final static byte[] FLAC_HEADER = new byte[]{0x66, 0x4C, 0x61, 0x43};
    public final static byte[] MP3_HEADER = new byte[]{0x49, 0x44, 0x33};
    public final static byte[] OGG_HEADER = new byte[]{0x4F, 0x67, 0x67, 0x53};
    public final static byte[] M4A_HEADER = new byte[]{0x66, 0x74, 0x79, 0x70};
    private static Gson gson = new Gson();

    public static boolean isArrayEquals(short[] a, short[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static short[] listToIntArray(List<Short> list) {
        short[] result = new short[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static void queryKeyInfo(byte[] keyData, String filename, String format) {
        String response = HttpUtil.urlRequestwithHeader("https://stats.ixarea.com/collect/qmcmask/query", HttpUtil.METHOD_POST, gson.toJson(new QueryKeyInfoRequestBody(format, keyData, filename)), new HashMap() {{
            put("Content-Type", "application/json");
        }});
        System.out.println(response);
    }

    public static String detectAudioExt(byte[] data, String fallbackExt) {
        if (isBytesEquals(MP3_HEADER, data, 0)) {
            return "mp3";
        }
        if (isBytesEquals(FLAC_HEADER, data, 0)) {
            return "flac";
        }
        if (isBytesEquals(OGG_HEADER, data, 0)) {
            return "ogg";
        }
        if (isBytesEquals(M4A_HEADER, data, 4)) {
            return "m4a";
        }
        return fallbackExt;
    }

    public static boolean isBytesEquals(byte[] a, byte[] b, int bStartPosition) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i + bStartPosition]) {
                return false;
            }
        }
        return true;
    }

    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }

    public static String Byte2String(byte nByte) {
        StringBuilder nStr = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            int j = (int) nByte & (int) (Math.pow(2, (double) i));
            if (j > 0) {
                nStr.append("1");
            } else {
                nStr.append("0");
            }
        }
        return nStr.toString();
    }

    public static class QueryKeyInfoRequestBody {
        private String Format;
        private byte[] Key;
        private String Filename;

        public QueryKeyInfoRequestBody(String format, byte[] key, String filename) {
            Format = format;
            Key = key;
            Filename = filename;
        }

        public String getFormat() {
            return Format;
        }

        public void setFormat(String format) {
            Format = format;
        }

        public byte[] getKey() {
            return Key;
        }

        public void setKey(byte[] key) {
            Key = key;
        }

        public String getFilename() {
            return Filename;
        }

        public void setFilename(String filename) {
            Filename = filename;
        }
    }

    public static class QueryKeyInfoResponseBody {

    }
}
