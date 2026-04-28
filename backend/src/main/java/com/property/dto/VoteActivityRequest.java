package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票问卷活动请求 DTO
 */
@Data
public class VoteActivityRequest {

    @NotBlank(message = "活动标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "活动类型不能为空")
    private Integer type;

    private LocalDateTime endTime;

    /**
     * 投票选项，仅业主投票使用
     */
    private List<String> options;
}
