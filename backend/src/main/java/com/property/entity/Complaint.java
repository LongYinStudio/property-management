package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投诉建议实体类
 */
@Data
@TableName("complaint")
public class Complaint implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private Integer type;
    
    private Integer status;
    
    private Long handlerId;
    
    private String reply;
    
    private LocalDateTime replyTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 类型
     */
    public static final int TYPE_COMPLAINT = 1;    // 投诉
    public static final int TYPE_SUGGESTION = 2;   // 建议
    
    /**
     * 状态
     */
    public static final int STATUS_PENDING = 0;    // 待处理
    public static final int STATUS_REPLIED = 1;    // 已回复
    public static final int STATUS_CLOSED = 2;     // 已关闭
}
