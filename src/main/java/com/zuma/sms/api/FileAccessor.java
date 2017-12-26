package com.zuma.sms.api;

import com.google.common.base.Charsets;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

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



	/**long numberGroupId,
	 * 指定号码组记录id,获取该号码组的文件, 指定 off 和 len
	 * @param off file的偏移量,指从该文件的第几个字符开始读取
	 * @param len 指读取几个字符
	 */
	@SneakyThrows
	public String readString(File file,long off,int len){
		try (InputStreamReader br = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
			//跳过指定数量的字符
			long skip = br.skip(off);
			//如果跳过的字符数,小于指定的偏移量,表示该偏移后无可读字符
			if(skip < off)
				return "";
			//缓冲数组
			char[] buf = new char[len];
			//每次读取到的字符数
			int charNum = br.read(buf, 0, buf.length);
			if(charNum == -1)
				return "";
			return StringUtils.trim(new String(buf));
		} catch (Exception e) {
			log.error("[文件存取器]读取文件字符异常.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.IO_ERROR);
		}
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
	public void writeBySendTaskId(long taskId,String phones,String errorInfo) {
		writeBySendTaskIdAndInfo(taskId,"errorPhone:" + phones + "---" + "errorInfo:"+errorInfo);
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
