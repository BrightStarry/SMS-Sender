package com.zuma.sms.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2018/1/10 0010 09:33
 * 助通 接口
 */
public interface ZhuTongAPI {
	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	class Request {
		private String username;//	用户名（必填）
		private String tkey;//	当前时间（必填,24小时制），格式：yyyyMMddHHmmss，例如：20160315130530。客户时间早于或晚于网关时间超过30分钟，则网关拒绝提交。
		/**
		 * 密码（必填）:md5( md5(password)  +  tkey) )
		 * 其中“+”表示字符串连接。即：先对密码进行md5加密，将结果与tkey值合并，再进行一次md5加密。
		 * 两次md5加密后字符串都需转为小写。
		 * 例如：若当前时间为2016-03-15 12:05:30，密码为123456，
		 * 则：password =md5(md5(“123456”) + “20160315120530” )
		 * 则：password =md5(e10adc3949ba59abbe56e057f20f883e20160315120530)
		 * 则：password = ea8b8077f748b2357ce635b9f49b7abe
		 */
		private String password;//
		private String mobile;//	手机号 (必填，最多支持2000个号码)
		private String content;//	发送内容（必填,最好不要包含空格和回车，最多支持500个字，一个内容里面只能包含一个签名，如：内容【签名】）
		private String xh;//	扩展的小号,必须为数字，没有请留空。注意：若为多签名用户，又无法确保所提交签名与扩展号一一对应,参数xh=0。所发送签名和扩展号需要提前报备，SMS短信平台系统会根据签名自动加上扩展号。

		public Request(String username, String tkey, String password, String mobile, String content) {
			this.username = username;
			this.tkey = tkey;
			this.password = password;
			this.mobile = mobile;
			this.content = content;
		}
	}

	//发送接口响应
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private String code;
		private String messageId;
	}

	//变量发送请求
	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	class VariateRequest {
		private String username;//	用户名（必填）
		private String tkey;//	当前时间（必填,24小时制），格式：yyyyMMddHHmmss，例如：20160315130530。客户时间早于或晚于网关时间超过30分钟，则网关拒绝提交。
		/**
		 * 密码（必填）:md5( md5(password)  +  tkey) )
		 * 其中“+”表示字符串连接。即：先对密码进行md5加密，将结果与tkey值合并，再进行一次md5加密。
		 * 两次md5加密后字符串都需转为小写。
		 * 例如：若当前时间为2016-03-15 12:05:30，密码为123456，
		 * 则：password =md5(md5(“123456”) + “20160315120530” )
		 * 则：password =md5(e10adc3949ba59abbe56e057f20f883e20160315120530)
		 * 则：password = ea8b8077f748b2357ce635b9f49b7abe
		 */
		private String password;//
		private String mobile;//手机号，多个手机号为用半角 , 分开，如13899999999,13688888888(最多200个，必填)
		private String content;//发送内容多个内容用※分开，如短信1【签名】※短信2【签名】（必填，最多200个，一个内容里面只能包含一个签名）
		private String productid;//	产品id(必填，不同的产品用于发不同类型的信息)
		private String xh;//	扩展的小号,必须为数字，没有请留空。注意：若为多签名用户，又无法确保所提交签名与扩展号一一对应,参数xh=0。所发送签名和扩展号需要提前报备，SMS短信平台系统会根据签名自动加上扩展号。
	}

	//发送短信异步回调
	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	@AllArgsConstructor
	class AsyncResponse {
		/**
		 * 消息id
		 */
		private String messageId;
		/**
		 * 手机号
		 */
		private String phone;
		/**
		 * 异常码 根据异常枚举
		 */
		private String code;
		/**
		 * 时间
		 */
		private Date time;
	}

	//短信上行
	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	@AllArgsConstructor
	class SmsUpResponse {
		private String msgid;//	回复的消息id
		private String mobile;//	回复的手机号码
		private String content;//	手机回复的内容
		private String xh;//	推送的用户小号
	}
}
