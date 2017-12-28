package com.zuma.sms.entity;

import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 短信发送记录
 */
@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class SmsSendRecord {
    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 平台发送记录id,当接口调用发送时
     */
    private Long platformSendSmsRecordId;

    /**
     * 平台id,当接口调用时
     */
    private Long platformId;

    /**
     * 任务id,,当用户操作发送时
     */
    private Long sendTaskId;

    /**
     * 通道id
     */
    private Long channelId;

    /**
     * 通道名
     */
    private String channelName;

    /**
     * 发送手机号
     */
    private String phones;

    /**
     * 手机号总数
     */
    private Integer phoneCount;


    /**
     * 发送消息
     */
    private String message;

    /**
     * 调用者请求对象json字符
     */
    private String requestBody;

    /**
     * 其他id,一般为接口返回的该次调用唯一标识
     */
    private String otherId;

    /**
     * 同步回调时间
     */
    private Date syncTime;

    /**
     * 同步返回对象json字符
     */
    private String syncResultBody;

    /**
     * 异步回调时间
     */
    private Date asyncTime;

    /**
     * 异步返回对象json字符
     */
    private String asyncResultBody;

    /**
     * 异常信息,如果有的话
     */
    private String errorInfo;

    /**
     * 状态. 0:默认;1:同步成功;2:异步成功;-1:同步失败;-2:异步失败
     */
    private Integer status = SmsSendRecordStatusEnum.DEFAULT.getCode();

    /**
     * 创建时间,也是发送时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


    public SmsSendRecord(Long sendTaskId, Long channelId, String channelName, String phones, Integer phoneCount, String message) {
        this.sendTaskId = sendTaskId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.phones = phones;
        this.phoneCount = phoneCount;
        this.message = message;
    }

    public SmsSendRecord(Long platformSendSmsRecordId, Long platformId, Long channelId, String channelName, String phones, Integer phoneCount, String message) {
        this.platformSendSmsRecordId = platformSendSmsRecordId;
        this.platformId = platformId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.phones = phones;
        this.phoneCount = phoneCount;
        this.message = message;
    }

    /**
     * 是否是定时任务发送记录
     */
    public boolean isTaskRecord() {
        return sendTaskId != null;
    }
}