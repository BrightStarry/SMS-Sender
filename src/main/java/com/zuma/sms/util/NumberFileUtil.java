package com.zuma.sms.util;

import com.zuma.sms.config.ConfigStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 14:08
 * 号码文件工具类
 */
@Component
@Slf4j
public class NumberFileUtil {

	private static ConfigStore configStore;

	@Autowired
	public void init(ConfigStore configStore){
		NumberFileUtil.configStore = configStore;
	}

	public static File getFileByNumberGroupId(long id) {
		return new File(configStore.numberGroupPre + id + ".txt");
	}
}
