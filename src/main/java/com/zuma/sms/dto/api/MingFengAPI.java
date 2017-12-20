package com.zuma.sms.dto.api;

import com.zuma.sms.util.DateUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 16:54
 * 铭锋接口
 */
public interface MingFengAPI {
	@Data
	@Accessors(chain = true)
	class Request {
		private String userId;//企业id-随便写
		private String account;//企业帐号
		private String password;//企业密码
		private String mobile;//手机号,用逗号
		/**
		 * 短信的内容，内容需要UTF-8编码，
		 * 提交内容格式：【签名】+内容。签名是公司的名字或者公司项目名称。
		 * 示例：【腾飞】您的验证码：1439。
		 * 【】是签名的标识符。请按照正规的格式提交内容测试，请用正规内容下发，
		 * 最好不要当成是测试，就当是正式使用在给自己的客户发信息，签名字数3-8个字
		 */
		private String content;//发送内容
		private String sendTime;//定时发送时间,空,立即发送
		private String action = "send";//发送任务命令 - 固定为 send
		private String extno;//扩展子号

		public Request(String userId, String account, String password, String mobile, String content) {
			this.userId = userId;
			this.account = account;
			this.password = password;
			this.mobile = mobile;
			this.content = content;
		}
	}


	//发送短信响应
	@Data
	class Response {
		private String returnStatus;//返回状态
		private String message;//消息
		private String remainpoint;//余额
		private String taskID;//id
		private String successCount;//成功数
	}

//	//主动请求异步回调
//	@Data
//	@Accessors(chain = true)
//	class AsyncRequest {
//		private String userid;//企业id
//		private String account;//用户帐号
//		private String password;//帐号密码
//		private Integer status;//每次拉取号码数
//		private String action = "query";//任务名,固定为query
//		private String taskId;//任务id,如果传此参数,只查询该批次的号码
//	}

//	//请求回来的数据
//	@Data
//	class AsyncResponse {
//		private String error;//错误码
//		private String remark;//备注
//		private List<AsyncResponseChild> statusbox;
//	}

	//短信发送异步回调
	@Data
	@XmlRootElement(name="returnsms")
	class AsyncResponse{
		private List<AsyncResponseChild> childs;

		@XmlElement(name = "statusbox")
		public List<AsyncResponseChild> getChilds() {
			return childs;
		}
	}


	//短信发送异步回调子对象
	@Data
	class AsyncResponseChild {
		private String mobile;//手机号
		private String taskid;//任务id
		private String status;//状态 10:发送成功; 20:发送失败
		private String receivetime;//接收时间 YYYY-MM-dd HH:mm:ss
		private String errorcode;//异常码....上级网关返回值，不同网关返回值不同，仅作为参考
		private String extno;//自定义扩展号
	}


	//短信上行
	@Data
	@XmlRootElement(name="returnsms")
	class SmsUpResponse{
		private List<SmsUpResponseChild> childs;

		@XmlElement(name = "callbox")
		public List<SmsUpResponseChild> getChilds() {
			return childs;
		}
	}

	//短信上行子对象
	@Data
	class SmsUpResponseChild {
		private String mobile;//手机号
		private String content;//内容
		private String receivetime;//接收时间
		private String extno;//扩展号


	}






}
