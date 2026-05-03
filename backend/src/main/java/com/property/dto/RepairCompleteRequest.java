package com.property.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 完成报修处理请求DTO
 */
@Data
public class RepairCompleteRequest {

    @Size(max = 1000, message = "处理结果长度不能超过1000位")
    private String handleResult;
}
