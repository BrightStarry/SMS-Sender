package com.zuma.sms.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zuma.sms.enums.error.ChangXiangErrorEnum;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/4 0004 15:28
 * 畅想发送短信api
 */
public interface ChangXiangAPI {

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
		private List<AsyncResponseChild> report;//状态报告

		/**
		 * 直接将字符传解析
		 * @param report
		 */
		public void setReport(String report) {
			String[] arr = StringUtils.split(report, ";");
			List<AsyncResponseChild> result = new ArrayList<>(arr.length);
			for (int i = 0; i < arr.length; i++) {
				String[] arr2 = StringUtils.split(arr[i], ",");
				result.add(new AsyncResponseChild(arr2[0],arr[1],arr[2],arr[3]));
			}
			this.report = result;
		}
	}

	//单个异步回调
	@Data
	@AllArgsConstructor
	class AsyncResponseChild {
		private String id;
		private String phone;
		private String code;
		private String time;


	}


	//异步回调 或 短信上行 的己方响应
	@Data
	class ResponseReturn{
		private String status = ChangXiangErrorEnum.SUCCESS.getCode();//状态 返回 success 或 error:客户端自定义错误
	}


	//上行推送
	@Data
	class SmsUpResponse {
		private String name;//帐号
		private String src;//手机号码
		private String dest;//下行协议带的ext参数
		private String content;//上行内容
		@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
		private Date time;//上行时间 格式: YYYY-MM-dd HH:mm:ss
		private String reference;//参考信息,值为下行提交时的reference参数值。该参数可为空
		private String sign;//下行时的签名
	}
}
