package com.zuma.sms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zuma.sms.api.ConcurrentManager;
import com.zuma.sms.api.socket.CMPPConnectionManager;
import com.zuma.sms.api.socket.IPPortPair;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 通道 实体类
 */
@DynamicUpdate
@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 通道名
     */
    private String name;

    /**
     * 排序,优先级,0-255
     */
    private Integer sort;

    /**
     * 缓存key,用于缓存
     */
    private String cacheName;

    /**
     * key,用于缓存/spring容器等
     */
    private String keyName;

    /**
     * 通道类型,对同一个ip的接口调用为同一类型
     */
    private Integer type;

    /**
     * 最大群发数
     */
    private Integer maxGroupNumber;

    /**
     * 支持的运营商. 0:未知;1:移动;2:联通;3.电信
     */
    private Integer supportOperator;

    /**
     * 最大连接数,针对socket
     */
    private Integer maxConnect;

    /**
     * 最大并发数,针对所有,类型相同,并发肯定一样
     */
    private Integer maxConcurrent;

    /**
     * 是否是cmpp
     */
    private Integer isCmpp = IntToBoolEnum.FALSE.getCode();

    /**
     * 连接用的字符若干
     */
    private String aKey;
    private String bKey;
    private String cKey;
    private String dKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


    /**
     * 并发管理器 非DB字段
     */
    @JsonIgnore
    @Transient
    private ConcurrentManager concurrentManager;

    /**
     * CMPP连接管理器 非DB字段
     */
    @JsonIgnore
    @Transient
    private CMPPConnectionManager cmppConnectionManager;

    /**
     * cmpp ip port 非DB字段
     */
    @JsonIgnore
    @Transient
    private IPPortPair ipPortPair;


    /**
     * 是否是cmpp
     */
    public boolean isCMPP() {
        return EnumUtil.equals(isCmpp, IntToBoolEnum.TRUE);
    }

}