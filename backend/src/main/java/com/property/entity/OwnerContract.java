package com.property.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业主合同实体类
 */
@Data
@TableName("owner_contract")
public class OwnerContract implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String contractNo;

    private String contractName;

    private Integer contractType;

    private String roomInfo;

    private LocalDate signDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal amount;

    private Integer status;

    private String attachmentName;

    private String attachmentUrl;

    private String remark;

    private Long creatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public static final int TYPE_CHECK_IN = 1;
    public static final int TYPE_DECORATION = 2;
    public static final int TYPE_PARKING = 3;
    public static final int TYPE_SERVICE = 4;
    public static final int TYPE_OTHER = 5;

    public static final int STATUS_PENDING = 1;
    public static final int STATUS_ACTIVE = 2;
    public static final int STATUS_EXPIRED = 3;
    public static final int STATUS_TERMINATED = 4;
}
