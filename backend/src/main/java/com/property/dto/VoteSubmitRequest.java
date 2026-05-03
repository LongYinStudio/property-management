package com.property.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 投票问卷提交请求 DTO
 */
@Data
public class VoteSubmitRequest {

    /**
     * 投票类型时使用
     */
    @Size(max = 100, message = "投票选项长度不能超过100位")
    private String selectedOption;

    /**
     * 意见征集时使用
     */
    @Size(max = 1000, message = "意见内容长度不能超过1000位")
    private String content;

    @AssertTrue(message = "投票选项和意见内容不能同时为空")
    public boolean hasSubmissionContent() {
        return StringUtils.hasText(selectedOption) || StringUtils.hasText(content);
    }
}
