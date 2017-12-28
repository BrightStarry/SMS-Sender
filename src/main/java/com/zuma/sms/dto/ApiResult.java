package com.zuma.sms.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/27 0027 17:38
 * 平台接口调用发送短信时  的 返回对象
 */
@Data
@Accessors(chain = true)
public class ApiResult {
	//失败的结果
	private List<ResultDTO<SendData>> errorResults;
	//失败数
	private Integer errorNum;

}
