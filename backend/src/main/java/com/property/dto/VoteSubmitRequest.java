package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 投票问卷提交请求 DTO
 */
@Data
public class VoteSubmitRequest {

    /**
     * 投票类型时使用
     */
    private String selectedOption;

    /**
     * 意见征集时使用
     */
    private String content;
}
