package com.zuma.sms.entity;

import com.zuma.sms.enums.db.IntToBoolEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 号码源
 */
@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class NumberSource {
    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 号码总数
     */
    private Integer numberCount;

    /**
     * 已分组的号码总数
     */
    private Integer groupedCount = 0;

    /**
     * 状态. 0:未分组; 1:已分组
     */
    private Integer status = IntToBoolEnum.FALSE.getCode();


    /**
     * 是否被删除. 0:否;1:是
     */
    private Integer isDelete =  IntToBoolEnum.FALSE.getCode();

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    public NumberSource(String name, String remark, Integer numberCount) {
        this.name = name;
        this.remark = remark;
        this.numberCount = numberCount;
    }
}