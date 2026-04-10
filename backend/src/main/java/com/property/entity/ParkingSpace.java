package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车位实体类
 */
@Data
@TableName("parking_space")
public class ParkingSpace implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String spaceNumber;
    
    private String location;
    
    private Integer type;
    
    private Integer status;
    
    private Long ownerId;
    
    private BigDecimal price;
    
    private BigDecimal rentPrice;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 车位类型
     */
    public static final int TYPE_NORMAL = 1;      // 普通车位
    public static final int TYPE_VIP = 2;         // VIP车位
    
    /**
     * 状态
     */
    public static final int STATUS_AVAILABLE = 0; // 空闲
    public static final int STATUS_RENTED = 1;    // 已出租
    public static final int STATUS_SOLD = 2;      // 已出售
}
