package com.zuma.sms.api;

import com.google.common.base.Charsets;
import com.zuma.sms.config.store.ConfigStore;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 10:10
 * 文件存取器
 */
@Component
@Slf4j
public class FileAccessor {
	@Autowired
	private ConfigStore configStore;

	private  static final String LINE_BREAK = System.getProperty("line.separator");

	/**
	 * 指定号码组记录id,获取该号码组的文件
	 */
	public ConcurrentLinkedQueue<String> readBySendTask(long numberGroupId){
		//文件路径
		File file = new File(configStore.numberGroupPre + numberGroupId + ".txt");
		//读取到所有手机号
		String tmp = readToString(file);
		//切割
		String[] split = StringUtils.split(tmp, ",");
		//转为并发队列
		return new ConcurrentLinkedQueue<>(Arrays.asList(split));
	}

	/**
	 * 指定 发送任务id,异常信息,将其写入文件
	 */
	public void writeBySendTaskIdAndInfo(long taskId,String data) {
		File file = new File(configStore.sendTaskErrorInfoPre + taskId + ".txt");
		writeStringToFile(file,data,true);
	}

	/**
	 * 指定 发送任务id,,将指定格式的异常信息写入文件
	 */
	public void writeBySendTaskId(long taskId,String phones) {
		writeBySendTaskIdAndInfo(taskId,"errorPhone:" + phones);
	}

	/**
	 * 将string写入文件,追加
	 * @param isLine 是否分行
	 */
	public void writeStringToFile(File file,String data,boolean isLine){
		try {
			FileUtils.writeStringToFile(file,
					  data + (isLine ? LINE_BREAK : ""),
					CharsetUtil.UTF_8,
					true);
		} catch (IOException e) {
			log.error("[文件存取器]写入文件失败.e:{}",e.getMessage(),e);
		}
	}


	/**
	 * 读取文件为string
	 */
	public String readToString(File file) {
		try {
			return  FileUtils.readFileToString(file, Charsets.UTF_8);
		} catch (IOException e) {
			log.error("[文件读取器],读取文件失败.e:{}",e.getMessage(),e);
		}
		return "";
	}


}
