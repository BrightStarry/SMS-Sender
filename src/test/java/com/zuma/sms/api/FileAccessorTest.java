package com.zuma.sms.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.*;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 15:14
 * 文件读取测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class FileAccessorTest {

	@Autowired
	private FileAccessor fileAccessor;

	@Test
	public void readString() throws Exception {
		File file = new File("D:" + File.separator + "d.txt");
		String s = fileAccessor.readString(file, 36, 10);
		System.out.println(s);
	}

}