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
    private Integer channelId;

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
    @Column(name = "request_body")
    private String requestBody;

    /**
     * 创建时间,也是上行时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;


}