package com.property.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访客登记实体
 */
@Data
@TableName("visitor_record")
public class VisitorRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerId;

    private String visitorName;

    private String visitorPhone;

    private Integer visitorCount;

    private String licensePlate;

    private String purpose;

    private LocalDateTime visitTime;

    private LocalDateTime validUntil;

    private String passCode;

    /**
     * 状态：0-待通行 1-已通行 2-已失效 3-已取消
     */
    private Integer status;

    private Long verifierId;

    private LocalDateTime verifyTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PASSED = 1;
    public static final int STATUS_EXPIRED = 2;
    public static final int STATUS_CANCELED = 3;
}
