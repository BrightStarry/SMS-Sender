package com.zuma.sms.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * author:ZhengXing
 * datetime:2018/1/2 0002 09:32
 * 手机工具类测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PhoneUtilTest {

	@Test
	public void getShortUrl() throws Exception {
		String a = PhoneUtil.getShortUrl("https://tianyiring.com/m/pop/18&02340710130052.html?a=UBVDNVRQMFTY", "", "0");
		System.out.println(a);
	}



}