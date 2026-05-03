package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 清洁任务请求DTO
 */
@Data
public class CleaningRequest {
    
    @NotBlank(message = "清洁位置不能为空")
    @Size(max = 100, message = "清洁位置长度不能超过100位")
    private String location;
    
    @Size(max = 1000, message = "清洁描述长度不能超过1000位")
    private String description;
    
    @Size(max = 1000, message = "图片地址长度不能超过1000位")
    private String images;
}
