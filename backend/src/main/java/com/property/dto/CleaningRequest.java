package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 清洁任务请求DTO
 */
@Data
public class CleaningRequest {
    
    @NotBlank(message = "清洁位置不能为空")
    private String location;
    
    private String description;
    
    private String images;
}
