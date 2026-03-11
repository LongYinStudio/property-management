package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户请求DTO
 */
@Data
public class UserRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private String phone;
    
    private String email;
    
    private String avatar;
    
    @NotNull(message = "角色不能为空")
    private Integer role;
    
    private Integer status;
    
    private Long communityId;
    
    private Long buildingId;
    
    private Long roomId;
}
