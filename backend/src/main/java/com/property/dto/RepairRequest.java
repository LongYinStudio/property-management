package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修请求DTO
 */
@Data
public class RepairRequest {
    
    @NotBlank(message = "报修标题不能为空")
    private String title;
    
    private String content;
    
    private String images;
    
    @NotNull(message = "报修类型不能为空")
    private Integer type;
}
