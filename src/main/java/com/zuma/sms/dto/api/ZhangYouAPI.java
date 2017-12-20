package com.zuma.sms.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 13:37
 * 掌游接口
 */
public interface ZhangYouAPI {

	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	class Request {
		private String sid;//* 合作商家的企业代码，每个合作商家仅有一个。由我方分配，形式如:10010001。
		private String cpid;//* 业务代码，用于区分同一合作商的不同业务。由我方分配，形式如:600100。
		private String mobi;//* 待发送的手机号码，多个号码用半角逗号隔开，但不能超过规定的个数（暂定为30个）。
		private String sign;//* 安全签名，生成方式：cpid+key的MD5加密（小写，32位）。
		//* 待发送短信内容，字符个数不能超过210个。
		// 计费方式：70字符一条。短信内容需要采用BASE64（参加附1：Base64.java）进行编码，然后URLEncode编码。
		// 其中socket方式需以字符串的形式传输。
		private String msg;
		private Integer spcode;//自定义加长接入号。(2位数字)
	}

	@Data
	@Builder
	class Response {
		String code;//返回码
		String id;//本次请求id
	}

	@Data
	@XmlRootElement(name = "MsgDataReport")
	class AsyncResponse {

		private String msgType;//消息类型
		private String msgCode;//消息代码
		private String msgContent;//消息内容
		private String mobileSource;//用户手机号
		private String timestamp;//时间戳
		private String spCode;//用户上行端口
		private String taskId;//对应下行任务编号,MsgType取值为report时，该节点有效，时间戳(17位)+3位随机数
		@XmlElement(name = "MsgType")
		public String getMsgType() {
			return msgType;
		}
		@XmlElement(name = "MsgCode")
		public String getMsgCode() {
			return msgCode;
		}
		@XmlElement(name = "MsgContent")
		public String getMsgContent() {
			return msgContent;
		}
		@XmlElement(name = "MobileSource")
		public String getMobileSource() {
			return mobileSource;
		}
		@XmlElement(name = "Timestamp")
		public String getTimestamp() {
			return timestamp;
		}
		@XmlElement(name = "SpCode")
		public String getSpCode() {
			return spCode;
		}
		@XmlElement(name = "TaskId")
		public String getTaskId() {
			return taskId;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@XmlRootElement(name = "MsgDataReportResp")
	class AsyncResponseReturn {
		@XmlElement(name = "ResultCode")
		private String code;

		@XmlElement(name = "ResultMSG")
		private String message;

		@XmlElement(name = "Timestamp")
		private String date;

		public AsyncResponseReturn(ZhangYouErrorEnum errorEnum, Date date) {
			this.code = errorEnum.getCode();
			this.message = errorEnum.getMessage();
			this.date = new SimpleDateFormat("yyyyMMddHHmmss").format(date);//可用@JsonFormat注解
		}

	}

}
