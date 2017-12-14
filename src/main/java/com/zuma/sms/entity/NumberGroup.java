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
 * 号码组 实体类
 */
@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class NumberGroup {
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
     * 号码组类别id
     */
    private Long typeId;

    /**
     * 号码组类别名
     */
    private String typeName;

    /**
     * 号码源id
     */
    private Long numberSourceId;

    /**
     * 号码源名称
     */
    private String numberSourceName;

    /**
     * 号码总数
     */
    private Integer numberCount;

    /**
     * 分组模式. 0:顺序;1:随机;2:手动
     */
    private Integer groupMode;

    /**
     * 是否被删除. 0:否;1:是
     */
//    private Integer isDelete = IntToBoolEnum.FALSE.getCode();

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}