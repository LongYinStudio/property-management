package com.property.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人信息请求DTO
 */
@Data
public class UpdateProfileRequest {
    
    @Size(max = 50, message = "真实姓名长度不能超过50位")
    private String realName;

    @Size(max = 500, message = "头像地址长度不能超过500位")
    private String avatar;
    
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100位")
    private String email;
}
