package com.zuma.sms.dto;

import com.zuma.sms.dto.api.MingFengAPI;
import com.zuma.sms.util.CodeUtil;
import lombok.Builder;
import lombok.Data;

/**
 * author:Administrator
 * datetime:2017/11/9 0009 15:41
 * 掌游同步发送后，需缓存的数据
 */
@Data
@Builder
public class APICacheDTO {
    private String id;//api流水号
    private Long otherId;//平台id或任务id
    private Long timestamp;//时间戳-sendSmsForm中平台发过来的-方便平台区分
    private String phones;//该次调用的手机号
    private String smsMessage;//该次调用的短信消息
    private Long recordId;//该次短信发送记录id

}
