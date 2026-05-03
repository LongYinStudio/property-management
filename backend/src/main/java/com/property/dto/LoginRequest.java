package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50位之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5-20位之间")
    private String password;
}
