package com.property.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50位之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5-20位之间")
    private String password;
    
    @Size(max = 50, message = "真实姓名长度不能超过50位")
    private String realName;
    
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100位")
    private String email;
    
    @Positive(message = "房间ID必须大于0")
    private Long roomId;
}
