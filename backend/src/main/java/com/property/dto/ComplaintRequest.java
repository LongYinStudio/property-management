package com.property.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 投诉建议请求DTO
 */
@Data
public class ComplaintRequest {
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100位")
    private String title;
    
    @Size(max = 1000, message = "内容长度不能超过1000位")
    private String content;
    
    @NotNull(message = "类型不能为空")
    @Min(value = 1, message = "类型值不正确")
    @Max(value = 2, message = "类型值不正确")
    private Integer type;
}
