package com.zuma.sms.dto.api;

import com.sun.xml.internal.fastinfoset.Encoder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URLDecoder;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 13:06
 * 创蓝
 */
public interface ChuangLanAPI {

	//普通发送接口请求
	@Data
	@Accessors(chain = true)
	class Request {
		private String account;//用户名 *
		private String password;//密码 *
		private String msg;//短信内容,长度不超过536个字符 *
		private String phone;//手机号码,逗号 *
		private String report = "true";//是否需要状态报告.(默认false)
		private String extend;//下发短信号码扩展码，纯数字，建议1-3位，选填
		private String uid;//该条短信在您业务系统内的ID，如订单号或者短信发送记录流水号，选填

		public Request(String account, String password, String msg, String phone, String uid) {
			this.account = account;
			this.password = password;
			this.msg = msg;
			this.phone = phone;
			this.uid = uid;
		}

		public Request(String account, String password, String msg, String phone) {
			this.account = account;
			this.password = password;
			this.msg = msg;
			this.phone = phone;
		}
	}

	//普通发送接口响应
	@Data
	class Response {
		private String time;//响应时间
		private String msgId;//消息id
		private String code;//状态码
		private String errorMsg;//状态码说明
	}


	//变量发送请求
	@Data
	class VariateRequest {
		private String account;//用户名 *
		private String password;//密码 *
		private String msg;//短信内容,长度不超过536个字符 *
		private String params;//手机号码和变量参数，多组参数使用英文分号;区分 *
		private String report = "true";//是否需要状态报告.(默认false)
		private String extend;//下发短信号码扩展码，纯数字，建议1-3位，选填
		private String uid;//该条短信在您业务系统内的ID，如订单号或者短信发送记录流水号，选填

		public VariateRequest(String account, String password, String msg, String params) {
			this.account = account;
			this.password = password;
			this.msg = msg;
			this.params = params;
		}

		public VariateRequest(String account, String password, String msg, String params, String uid) {
			this.account = account;
			this.password = password;
			this.msg = msg;
			this.params = params;
			this.uid = uid;
		}
	}

	//变量发送响应
	@Data
	class VariateResponse {
		private String 	failNum;//失败条数
		private String time;//响应时间
		private String successNum;//成功条数
		private String msgId;//消息id
		private String errorMsg;//状态码说明
		private String code;//状态码
	}


	//发送短信回调请求
	@Data
	@Slf4j
	class AsyncResponse {
		private String receiver;//配置的一个用户名,
		private String pswd;//接收验证的密码
		private String msgId;//消息id
		@DateTimeFormat(pattern = "YYMMddHHmm")
		private Date reportTime;//运营商返回的状态更新时间，格式YYMMddHHmm，其中YY=年份的最后两位（00-99）
		private String mobile;//手机号
		@DateTimeFormat(pattern = "yyyyMMddHHmmss")
		private String notifyTime;// 253平台收到运营商回复状态报告的时间，格式yyyyMMddHHmmss
		private String uid;//用户在提交该短信时提交的uid参数，未提交则无该参数
		private String status;//状态 ,也就是code
		private String statusDesc;//状态说明，内容经过URLEncode编码(UTF-8)

		public void setStatusDesc(String statusDesc) {
			try {
				this.statusDesc = URLDecoder.decode(statusDesc, Encoder.UTF_8);
			} catch (Exception e) {
				log.error("[创蓝接口-短信异步回调]statusDesc解码异常.e:{}",e.getMessage(),e);
			}
		}
	}

	//短信上行
	@Data
	@Slf4j
	class SmsUpResponse {
		private String receiver;//配置的一个用户名,
		private String pswd;//接收验证的密码
		@DateTimeFormat(pattern = "YYMMddHHmm")
		private Date moTime;//上行时间，格式yyMMddHHmm，其中yy=年份的最后两位（00-99）
		private String mobile;//手机号
		private String msg;//上行内容,内容经过URLEncode编码(UTF-8)
		private String destcode;//运营商通道码
		private String spCode;//平台通道码
		@DateTimeFormat(pattern = "yyyyMMDDhhmmss")
		private Date notifyTime;//253平台收到运营商回复上行短信的时间，格式yyyyMMDDhhmmss
		private Integer isems;//是否为长短信的一部分，1:是，0，不是。不带该参数，默认为普通短信
		private String emshead;//isems为1时，本参数以ASCII码形式显示长短信的头信息。
		// 用“,”隔开，分为三个部分，第一部分标识该条长短信的ID（该ID为短信中心生成）；
		// 第二部分，表明该长短信的总条数（pk_total）；第三部分，该条短信为该长短信的第几条(pk_number)。
		// 例如：234,4,1，该短信的ID为234,该长短信的总长度为4条，1，当前为第一条。

		public void setMsg(String msg) {
			try {
				this.msg = URLDecoder.decode(msg, Encoder.UTF_8);
			} catch (Exception e) {
				log.error("[创蓝接口-短信上行推送]msg解码异常.e:{}",e.getMessage(),e);
			}
		}
	}
}
