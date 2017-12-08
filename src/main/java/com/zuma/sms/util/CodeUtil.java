package com.zuma.sms.util;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.zuma.sms.pool.CommonPool;
import com.zuma.sms.pool.GsonPool;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * author:ZhengXing
 * datetime:2017/11/7 0007 16:02
 * md5工具类
 */
public class CodeUtil {

    private static final CommonPool<Gson> gsonFactory = GsonPool.getInstance();


    //BASE64
    /**
     * 一个8位串的最大长度
     */
    static private final int BASELENGTH = 255;
    /**
     * BASE64编码长度
     */
    static private final int LOOKUPLENGTH = 64;
    /**
     * BASE64数组
     */
    public static final byte[] base64_alphabet = new byte[LOOKUPLENGTH];
    /**
     * BASE64反转数组
     */
    public static final byte[] base64_inv = new byte[BASELENGTH];

    static {
        for (int i = 0; i < BASELENGTH; i++) {
            base64_inv[i] = -1;
        }
        // A-Z从0-25
        for (int i = 'Z'; i >= 'A'; i--) {
            base64_inv[i] = (byte) (i - 'A');
        }
        // a-z是从26-51
        for (int i = 'z'; i >= 'a'; i--) {
            base64_inv[i] = (byte) (i - 'a' + 26);
        }
        // 0-9是从52-61
        for (int i = '9'; i >= '0'; i--) {
            base64_inv[i] = (byte) (i - '0' + 52);
        }
        base64_inv['+'] = 62;
        base64_inv['/'] = 63;
        for (int i = 0; i <= 25; i++) {
            base64_alphabet[i] = (byte) ('A' + i);
        }
        for (int i = 26, j = 0; i <= 51; i++, j++) {
            base64_alphabet[i] = (byte) ('a' + j);
        }
        for (int i = 52, j = 0; i <= 61; i++, j++) {
            base64_alphabet[i] = (byte) ('0' + j);
        }
        base64_alphabet[62] = '+';
        base64_alphabet[63] = '/';
    }

    /**
     * string 转 byte，使用utf8编码
     *
     * @param str
     * @return
     */
    public static byte[] stringToByteOfUTF8(String str) {
        return str.getBytes(Charsets.UTF_8);
    }

    /**
     * byte转 string，utf-8
     *
     * @param b
     * @return
     */
    public static String byteToStringOfUTF8(byte[] b) {
        return new String(b, Charsets.UTF_8);
    }

    /**
     * string 转 base64形式的 string
     *
     * @param str
     * @return
     */
    public static String stringToBase64(String str) {
        byte[] bytes = byteToBase64(stringToByteOfUTF8(str));
        return byteToStringOfUTF8(bytes);
    }

    /**
     * string形式的base64 转 string
     *
     * @param str
     * @return
     */
    public static String base64ToString(String str) {
        byte[] bytes = base64ToByte(stringToByteOfUTF8(str));
        return byteToStringOfUTF8(bytes);
    }

    /**
     * 转换BYTE数组为BASE形式
     *
     * @param bt
     * @return
     */
    public static byte[] byteToBase64(byte[] bt) {
        // 补齐为3的倍数的数据源集合
        byte[] btcl = new byte[bt.length
                + (bt.length % 3 == 0 ? 0 : (bt.length % 3 == 1 ? 2 : 1))];
        // 结果集的长度，数据源集合按照6BIT一组拆分后的大小,原来是8BIT一组
        byte[] r = new byte[btcl.length * 8 / 6];
        int j = 0;
        System.arraycopy(bt, 0, btcl, 0, bt.length);
        // 每次取3个字节进行操作
        for (int c = 0; c < bt.length; c += 3) {
            // 不做折行处理
            int n = ((btcl[c] << 16) & 0xff0000)
                    | ((btcl[c + 1] << 8) & 0xff00) | (btcl[c + 2] & 0xff);
            r[j] = base64_alphabet[n >>> 18 & 63];
            r[j + 1] = base64_alphabet[n >>> 12 & 63];
            r[j + 2] = base64_alphabet[n >>> 6 & 63];
            r[j + 3] = base64_alphabet[n & 63];
            j += 4;
        }
        for (int i = bt.length, k = 0; i < btcl.length; i++, k++) {
            r[r.length - 1 - k] = (byte) '=';
        }
        return r;
    }

    /**
     * 进行BASE64解码
     * bt的长度满足下面公式 bt.length = 3n*8/6
     * 即:bt的长度必定为4的倍数，必须能被4整除
     *
     * @param bt
     * @return
     */
    public static byte[] base64ToByte(byte[] bt) {
        // 做个基本的验证
        if (bt.length % 4 != 0) {
            throw new NumberFormatException("输入的BASE64编码不合法");
        }
        // 存放解码结果,大小为BASE64编码的BYTE数组的6倍除以8
        byte[] r = new byte[bt.length * 6 / 8];
        int i, k = 0;
        // 如果最后2位是'=',转化为'A'
        for (i = 0; i < 2; i++) {
            if (bt[bt.length - 1 - i] == (byte) '=') {
                bt[bt.length - 1 - i] = (byte) 'A';
                k++;
            }
        }
        int j = 0;
        for (int c = 0; c < bt.length; c += 4) {
            int n = (base64_inv[bt[c]] << 18) | (base64_inv[bt[c + 1]] << 12)
                    | (base64_inv[bt[c + 2]] << 6) | base64_inv[bt[c + 3]];
            r[j] = (byte) (n >>> 16 & 0xff);
            r[j + 1] = (byte) (n >>> 8 & 0xff);
            r[j + 2] = (byte) (n & 0xff);
            j += 3;
        }
        byte[] result = new byte[r.length - k];
        System.arraycopy(r, 0, result, 0, result.length);
        return result;
    }

    //MD5

    /**
     * 字符串转MD5，32位，小写
     *
     * @param str
     * @return
     */
    public static String stringToMd5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    /**
     * bytes转MD5，16
     *
     * @param bytes
     * @return
     */
    public static byte[] byteToMd5(byte[] bytes) {
        return DigestUtils.md5Digest(bytes);
    }



    /**
     * 字符串转MD5，16位，小写
     *
     * @param str
     * @return
     */
    public static String stringToMd5For16Bit(String str) {
        return stringToMd5(str).substring(8, 24);
    }


    //URLENCODER

    /**
     * string 转 urlencode
     *
     * @param str
     * @return
     */
    public static String stringToUrlEncode(String str) {
        String result = null;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //不可能发生。这个类设计有问题。传的不是Charset,而是String
        }
        return result;
    }

    /**
     * urlencode 转 string
     *
     * @param string
     * @return
     */
    public static String urlEncodeToString(String string) {
        String result = null;
        try {
            URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //不可能发生。这个类设计有问题。传的不是Charset,而是String
        }
        return result;
    }

    //JSON

    /**
     * 对象转json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objectToJsonString(T obj) {
        Gson gson = null;
        try {
            gson = gsonFactory.borrow();
            return gson.toJson(obj);
        } finally {
            gsonFactory.returnObj(gson);
        }

    }

    /**
     * json字符串转对象
     *
     * @param jsonString
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToObject(String jsonString, Class<T> tClass) {
        Gson gson = null;
        try {
            gson = gsonFactory.borrow();
            return gson.fromJson(jsonString, tClass);
        } finally {
            gsonFactory.returnObj(gson);
        }
    }


    /**
     * int整数转换为4字节的byte数组
     *
     * @param i 整数
     * @return byte数组
     */
    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    /**
     * byte数组转int
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }


}
