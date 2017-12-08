package com.zuma.sms.dto.api;

import lombok.Builder;
import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 13:27
 * 宽信接口
 */
public interface KuanXinAPI {

	@Data
	@Builder
	class Request {
		private String userId;//用户编号，接入方信息唯一标识
		private Long ts;//时间戳, 5分钟内有效, 时间戳是指格林威治时间1970年01月01日00时00分00秒起至现在的总毫秒数
		private String sign;//三个信息字符串拼接，然后md5算法加密 （MD5用32位，值必须要小写进行加密）md5(userid + ts + apikey) 中间无需空格
		private String mobile;//需要发送的手机号(多个号码以英文逗号 “,”分隔) 一次性最多1000个号码
		private String msgcontent;//短信内容 需要用urlencoder的utf-8编码 示例: java：URLEncode.encode(content,“utf-8”)1.普通短信70字 2.长短信350字
		private String extnum;//下发扩展号（1-4位）4个字节以内
		private String time;//发送时间(为空表示立即发送，如果定时发送，则需要按yyyyMMddHHmmss格式，如：20110115105822)
	}

	@Data
	class Response {
		private String code;//状态码
		private String msg;//状态描述
		private Data data;//数据节点
		/**
		 * 宽信发送短信接口响应对象中的data子对象
		 */
		@lombok.Data
		public class Data{
			private String taskId;//任务ID,接口返回的taskid，如果接口返回非0，则不返回data节点
		}
	}

	@Data
	class AsyncResponse{
		private String taskId;//id
		private String code;//状态码
		private String msg;//消息
		private String mobile;//用户手机号
		private String time;//接收时间，需要按yyyyMMddHHmmss 格式，如：20110115105822
	}

	@Data
	class SmsUpResponse{
		private String id;//唯一序列号
		private String mobile;//用户上行手机号码，如：13505710000
		private String srcId;//接收号码，平台提供的接入号
		private String msgContent;//接收内容，用户上行内容信息
		private String time;//接收时间，需要按yyyyMMddHHmmss格式，如：20110115105822)
	}
}
