package com.zuma.sms.entity;

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
     * 平台id,当接口调用发送时
     */
    @Column(name = "platform_id")
    private Long platformId;

    /**
     * 平台名,当接口调用发送时
     */
    private String platformName;

    /**
     * 任务id,,当用户操作发送时
     */
    @Column(name = "send_task_id")
    private Long sendTaskId;

    /**
     * 通道id
     */
    @Column(name = "channel_id")
    private Long channelId;

    /**
     * 通道名
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * 发送手机号
     */
    private String phones;

    /**
     * 调用者请求对象json字符
     */
    @Column(name = "request_body")
    private String requestBody;

    /**
     * 其他id,一般为接口返回的该次调用唯一标识
     */
    @Column(name = "other_id")
    private String otherId;

    /**
     * 同步回调时间
     */
    @Column(name = "sync_time")
    private Date syncTime;

    /**
     * 同步返回对象json字符
     */
    @Column(name = "sync_result_body")
    private String syncResultBody;

    /**
     * 异步回调时间
     */
    @Column(name = "async_time")
    private Date asyncTime;

    /**
     * 异步返回对象json字符
     */
    @Column(name = "async_result_body")
    private String asyncResultBody;

    /**
     * 状态. 0:默认;1:同步成功;2:异步成功;-1:同步失败;-2:异步失败
     */
    private Integer status;

    /**
     * 创建时间,也是发送时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    public SmsSendRecord(Long sendTaskId, Long channelId, String channelName, String phones, String requestBody) {
        this.sendTaskId = sendTaskId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.phones = phones;
        this.requestBody = requestBody;
    }
}