package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物业费实体类
 */
@Data
@TableName("property_fee")
public class PropertyFee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long roomId;
    
    private Integer year;
    
    private Integer month;
    
    private BigDecimal amount;
    
    private Integer type;
    
    private Integer status;
    
    private String description;
    
    private LocalDateTime payTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 费用类型
     */
    public static final int TYPE_PROPERTY = 1;     // 物业费
    public static final int TYPE_PARKING = 2;      // 停车费
    public static final int TYPE_WATER = 3;        // 水费
    public static final int TYPE_ELECTRIC = 4;     // 电费
    
    /**
     * 状态
     */
    public static final int STATUS_UNPAID = 0;     // 未支付
    public static final int STATUS_PAID = 1;       // 已支付
}
