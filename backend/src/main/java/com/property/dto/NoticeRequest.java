package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通知公告请求DTO
 */
@Data
public class NoticeRequest {

    @NotBlank(message = "公告标题不能为空")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    @NotNull(message = "公告类型不能为空")
    private Integer type;

    @NotNull(message = "是否置顶不能为空")
    private Integer isTop;
}
