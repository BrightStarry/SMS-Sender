package com.zuma.sms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class SmsUpRecord {
    /**
     * 记录id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 通道id
     */
    private Long channelId;

    /**
     * 通道名
     */
    private String channelName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 请求对象json字符
     */
    private String requestBody;

    /**
     * 上行时间
     */
    private Date upTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    public SmsUpRecord(Long channelId, String channelName, String phone, String content, String requestBody, Date upTime) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.phone = phone;
        this.content = content;
        this.requestBody = requestBody;
        this.upTime = upTime;
    }
}