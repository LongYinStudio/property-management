package com.property.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 报修请求DTO
 */
@Data
public class RepairRequest {
    
    @NotBlank(message = "报修标题不能为空")
    @Size(max = 100, message = "报修标题长度不能超过100位")
    private String title;
    
    @Size(max = 1000, message = "报修内容长度不能超过1000位")
    private String content;
    
    @Size(max = 1000, message = "图片地址长度不能超过1000位")
    private String images;
    
    @NotNull(message = "报修类型不能为空")
    @Min(value = 1, message = "报修类型不正确")
    @Max(value = 4, message = "报修类型不正确")
    private Integer type;
}
