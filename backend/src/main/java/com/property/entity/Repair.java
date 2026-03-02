package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报修实体类
 */
@Data
@TableName("repair")
public class Repair implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private String images;
    
    private Integer type;
    
    private Integer status;
    
    private Long handlerId;
    
    private String handleResult;
    
    private LocalDateTime handleTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 报修类型
     */
    public static final int TYPE_WATER = 1;        // 水管
    public static final int TYPE_ELECTRIC = 2;     // 电路
    public static final int TYPE_DOOR = 3;         // 门窗
    public static final int TYPE_OTHER = 4;        // 其他
    
    /**
     * 状态
     */
    public static final int STATUS_PENDING = 0;    // 待处理
    public static final int STATUS_PROCESSING = 1; // 处理中
    public static final int STATUS_COMPLETED = 2;  // 已完成
    public static final int STATUS_CLOSED = 3;     // 已关闭
}
