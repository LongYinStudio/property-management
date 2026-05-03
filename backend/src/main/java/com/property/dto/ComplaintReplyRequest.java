package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 回复投诉/建议请求DTO
 */
@Data
public class ComplaintReplyRequest {

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 1000, message = "回复内容长度不能超过1000位")
    private String reply;
}
