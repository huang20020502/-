package com.xin.yygh.hosp.entities;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医院设置表
 * @TableName hospital_set
 */
@TableName(value ="hospital_set")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalSet implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 医院名称
     */
    private String hosname;

    /**
     * 医院编号
     */
    private String hoscode;

    /**
     * api基础路径
     */
    private String apiUrl;

    /**
     * 签名秘钥
     */
    private String signKey;

    /**
     * 联系人
     */
    private String contactsName;

    /**
     * 联系人手机
     */
    private String contactsPhone;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除(1:已删除，0:未删除)
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}