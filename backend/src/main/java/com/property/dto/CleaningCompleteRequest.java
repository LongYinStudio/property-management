package com.property.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 完成清洁任务请求DTO
 */
@Data
public class CleaningCompleteRequest {

    @Size(max = 1000, message = "清洁结果长度不能超过1000位")
    private String cleanResult;
}
