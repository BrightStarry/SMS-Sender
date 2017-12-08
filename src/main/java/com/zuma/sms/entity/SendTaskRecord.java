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
 * 发送任务
 */
@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class SendTaskRecord {
    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建任务的用户id
     */
    @Column(name = "user_id")
    private Long userId;

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
     * 号码组id
     */
    @Column(name = "number_gruop_id")
    private Long numberGruopId;

    /**
     * 号码组名字
     */
    @Column(name = "number_group_name")
    private String numberGroupName;

    /**
     * 短信内容id
     */
    @Column(name = "sms_content_id")
    private Long smsContentId;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 开启线程数
     */
    @Column(name = "thread_count")
    private Byte threadCount;

    /**
     * 期望发送时间
     */
    @Column(name = "expect_start_time")
    private Date expectStartTime;

    /**
     * 期望结束时间
     */
    @Column(name = "expect_end_time")
    private Date expectEndTime;

    /**
     * 实际发送时间
     */
    @Column(name = "real_start_time")
    private Date realStartTime;

    /**
     * 实际结束时间
     */
    @Column(name = "real_end_time")
    private Date realEndTime;

    /**
     * 总用时(秒)
     */
    private Integer totalTime;

    /**
     * 状态. 0:等待中;1:运行中;2:成功;-1:部分失败;-2:全部失败;
     */
    private Integer status;

    /**
     * 号码总数
     */
    private Integer numberNum;

    /**
     * 成功数,异步成功
     */
    private Integer successNum;

    /**
     * 失败数,异步失败
     */
    private Integer failedNum;

    /**
     * 同步未响应数
     */
    private Integer syncUnResponse;

    /**
     * 异步未响应数
     */
    private Integer asyncUnResponse;

    /**
     * 已操作总数(可能被中断导致后续号码未操作)
     */
    private Integer usedNum;

    /**
     * 失败号码路径,可供下载
     */
    private String errorNumberPath;

    /**
     * 失败信息,可能为空
     */
    private String errorInfo;

    /**
     * 是否被删除. 0:否;1:是
     */
    private Byte isDelete;

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

}