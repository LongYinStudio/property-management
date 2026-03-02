package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 清洁任务实体类
 */
@Data
@TableName("cleaning")
public class Cleaning implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String location;
    
    private String description;
    
    private String images;
    
    private Integer status;
    
    private Long cleanerId;
    
    private String cleanResult;
    
    private LocalDateTime cleanTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 状态
     */
    public static final int STATUS_PENDING = 0;    // 待处理
    public static final int STATUS_PROCESSING = 1; // 处理中
    public static final int STATUS_COMPLETED = 2;  // 已完成
}
