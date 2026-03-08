package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 投诉建议请求DTO
 */
@Data
public class ComplaintRequest {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String content;
    
    @NotNull(message = "类型不能为空")
    private Integer type;
}
