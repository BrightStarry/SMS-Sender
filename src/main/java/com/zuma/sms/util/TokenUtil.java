package com.zuma.sms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author:ZhengXing
 * datetime:2017/10/18 0018 10:02
 * 令牌工具类
 */
@Component
public class TokenUtil {

    private static PasswordEncoder passwordEncoder;

    @Autowired
    public void init(PasswordEncoder passwordEncoder) {
        TokenUtil.passwordEncoder = passwordEncoder;
    }

    private static final AtomicInteger sequence = new AtomicInteger(1);

    /**
     * 生成每个平台唯一的令牌
     */
    public static String generate() {
        String random = RandomUtil.generateRandomNumber(10) + System.currentTimeMillis();
        return passwordEncoder.encode(random);
    }

    /**
     * 生成唯一索引
     */
    public static Integer getSequenceId() {
        int sequenceId = sequence.getAndIncrement();
        //序列最大值,超过后,复位序列
        if (sequenceId > Integer.MAX_VALUE - 1000) {
            synchronized (sequence) {
                //其set()方法不是原子方法.
                sequence.set(1);
            }
        }

        return sequenceId;
    }

    public static void main(String[] args) {
        String str = "a" + "17826824998,17826824998,17826824998" + "111111";
        String s = DigestUtils.md5DigestAsHex(str.getBytes());
        System.out.println(s);
    }
}
