package com.zuma.sms.util;

import com.zuma.sms.dto.api.cmpp.CMPPHeader;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author:ZhengXing
 * datetime:2017/11/24 0024 11:34
 * 筑望接口相关的 工具类
 */
@Slf4j
public class CMPPUtil {

    private static final AtomicInteger sequence = new AtomicInteger(10000);
    private static final Integer SEQUENCE_MAX_NUM = Integer.MAX_VALUE - 10000;

    /**
     * 自增序列-线程安全
     */
    public static Integer getSequenceId() {
        int sequenceId = sequence.getAndIncrement();
        //序列最大值,超过后,复位序列
        if (sequenceId > SEQUENCE_MAX_NUM) {
            synchronized (sequence) {
                //其set()方法不是原子方法.
                sequence.set(1);
            }
        }

        return sequenceId;
    }


    /**
     * 向流中写入指定字节长度的字符串，不足时补0
     *
     * @param dous:要写入的流对象
     * @param s:要写入的字符串
     * @param len:写入长度,不足补0
     */
    public static void writeString(DataOutputStream dous, String s, int len) {

        try {
            byte[] data = s.getBytes("gb2312");
            if (data.length > len) {
                log.error("向流中写入的字符串超长！要写" + len + " 字符串是:" + s);
            }
            int srcLen = data.length;
            dous.write(data);
            //如果长度不足,补若干个0
            while (srcLen < len) {
                dous.write('\0');
                srcLen++;
            }
        } catch (IOException e) {
            log.error("向流中写入指定字节长度的字符串失败:{}", e.getMessage(), e);
        }
    }

    /**
     * 从流中读取指定长度的字节，转成字符串返回
     *
     * @param ins:要读取的流对象
     * @param len:要读取的字符串长度
     * @return:读取到的字符串
     */
    public static String readString(DataInputStream ins, int len) {
        byte[] b = new byte[len];
        try {
            ins.read(b);
            String s = new String(b);
            s = s.trim();
            return s;
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 创建字符串,并截取空格
     */
    public static String getTrimString(byte[] bytes) {
        return new String(bytes).trim();
    }

    /**
     * 从InputStream中读取x个字符放入数组
     */
    public static byte[] getBytesForInput(InputStream inputStream,int len) throws IOException {
        byte[] temp = new byte[len];
        inputStream.read(temp);
        return temp;
    }

    /**
     *  从InputStream中读取x个字符放入数组,并创建字符串,并截取空格
     */
    public static String getStringForInput(InputStream inputStream,int len) throws IOException {
        return getTrimString(getBytesForInput(inputStream, len));
    }

    /**
     * 从输入流中读取字节注入CMPPHeader对象
     */
    public static void setHeader(DataInputStream inputStream, CMPPHeader header) throws IOException {
        header.setTotalLength(inputStream.readInt());
        header.setCommandId(inputStream.readInt());
        header.setSequenceId(inputStream.readInt());
    }



    public static void main(String[] args) {
//        0 bb9bb8ac4b2e72ff zuma#387843
//        byte[] bytes = "000000000".getBytes();
        String s = new String(CodeUtil.byteToMd5("34434234rfgdfgf".getBytes()));
        System.out.println(s);
    }
}
