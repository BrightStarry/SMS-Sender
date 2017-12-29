package com.zuma.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2017/12/29 0029 10:58
 * 发送任务 的 结果队列中保存的数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class ResultQueueDTO {
	private ResultDTO<SendData> resultDTO;
	private Long smsSendRecordId;

}
