package com.zuma.sms;

import com.zuma.sms.api.FileAccessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 16:15
 * 文件读取测试
 */
//@SpringBootTest
//@RunWith(SpringRunner.class)
public class FileReadTest {
	/**
	 * 写入号码
	 *
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		long a = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();
		RandomStringGenerator build = new RandomStringGenerator.Builder()
				.withinRange('0', '9').build();
		for (int i = 0; i < 100000; i++) {
			sb.append(build.generate(11)).append(",");
		}

		FileUtils.writeStringToFile(new File("D:/c.txt"), sb.toString(), "UTF-8");

		System.out.println("耗时:" + (System.currentTimeMillis() - a));
	}

	/**
	 * 读取
	 */
	@Test
	public void test1() {
		long a = System.currentTimeMillis();
		FileAccessor fileAccessor = new FileAccessor();
		String s = fileAccessor.readToString(new File("D:/a.txt"));
		String[] split = StringUtils.split(s, ",");
		//转为并发队列
		ConcurrentLinkedQueue<String> phones = new ConcurrentLinkedQueue<>(Arrays.asList(split));
		System.out.println(System.currentTimeMillis() - a);
//		System.out.println(s);
	}
}
