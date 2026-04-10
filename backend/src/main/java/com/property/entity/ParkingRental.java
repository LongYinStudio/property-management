package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 车位租用记录实体类
 */
@Data
@TableName("parking_rental")
public class ParkingRental implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long spaceId;
    
    private Long userId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal amount;
    
    private Integer status;
    
    private LocalDateTime payTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 状态
     */
    public static final int STATUS_UNPAID = 0;    // 待支付
    public static final int STATUS_PAID = 1;      // 已支付
    public static final int STATUS_EXPIRED = 2;   // 已过期
}
