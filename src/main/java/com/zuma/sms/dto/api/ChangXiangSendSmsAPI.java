package com.zuma.sms.dto.api;

import lombok.*;

/**
 * author:ZhengXing
 * datetime:2017/12/4 0004 15:28
 * 畅想发送短信api
 */
public interface ChangXiangSendSmsAPI {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = false)
	class Request {
		private String name;//帐号
		private String seed;//当前时间
		private String key;//md5(md5(password)+seed))
		private String dest;//手机号
		private String content;//短信内容
		private String reference;//参考信息
		private String delay;//定时参数
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = false)
	class Response{
		private String isSuccessStr;//是否成功字符 "success" 或 "error"
		private String idOrCode;//成功消息编号 或 错误码
	}

	//异步回调
	@Data
	class AsyncResponse {
		private String name;//帐号
		/**
		 * 一条状态报告格式：消息编号、手机号码、状态报告，提交时间，用半角逗号分割。
		 * 多条：每条状态报告间用半角分号分割。
		 * 每次最多20条。
		 * 例如：（没有换行）
		 * 1203110100547341956,13882768136,DELIVRD,2009-12-0311:05:38;
		 * 1203110100547341956,13124569889,DELIVRD,2009-12-0311:05:38
		 */
		private String report;//状态报告
	}

	//单个异步回调
	@Data
	class AsyncResponseChild {
		private String id;
		private String phone;
		private String code;
		private String time;
	}


	//响应异步回调的对象
	@Data
	class AsyncResponseResponse{
		private String status;//状态 返回 success 或 error:客户端自定义错误
	}
}
