package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("sys_user")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String email;
    
    private Integer role;
    
    private Integer status;
    
    private Long communityId;
    
    private Long buildingId;
    
    private Long roomId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    /**
     * 角色常量
     */
    public static final int ROLE_ADMIN = 1;        // 管理员
    public static final int ROLE_STAFF = 2;        // 物业人员
    public static final int ROLE_OWNER = 3;        // 业主
    
    /**
     * 状态常量
     */
    public static final int STATUS_ENABLE = 1;     // 启用
    public static final int STATUS_DISABLE = 0;    // 禁用
}
