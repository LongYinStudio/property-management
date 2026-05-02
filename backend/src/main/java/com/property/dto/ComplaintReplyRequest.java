package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 回复投诉/建议请求DTO
 */
@Data
public class ComplaintReplyRequest {

    @NotBlank(message = "回复内容不能为空")
    private String reply;
}
