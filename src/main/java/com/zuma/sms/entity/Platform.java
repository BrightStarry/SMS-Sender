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
 * 平台
 */
@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Platform {
    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 平台名字
     */
    private String name;

    /**
     * 令牌
     */
    private String token;

    /**
     * 回调url
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    /**
     * 状态： 0：停止授权；1：启用授权
     */
    private Byte status;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;


}