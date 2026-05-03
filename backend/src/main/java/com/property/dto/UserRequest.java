package com.property.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户请求DTO
 */
@Data
public class UserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50位之间")
    private String username;
    
    @Pattern(regexp = "^$|^.{5,20}$", message = "密码长度必须在5-20位之间")
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50位")
    private String realName;
    
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100位")
    private String email;
    
    @Size(max = 500, message = "头像地址长度不能超过500位")
    private String avatar;
    
    @NotNull(message = "角色不能为空")
    @Min(value = 1, message = "角色值不正确")
    @Max(value = 3, message = "角色值不正确")
    private Integer role;
    
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 1, message = "状态值不正确")
    private Integer status;
    
    @Positive(message = "小区ID必须大于0")
    private Long communityId;
    
    @Positive(message = "楼栋ID必须大于0")
    private Long buildingId;
    
    @Positive(message = "房间ID必须大于0")
    private Long roomId;
}
