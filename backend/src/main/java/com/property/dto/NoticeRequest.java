package com.property.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 通知公告请求DTO
 */
@Data
public class NoticeRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题长度不能超过100位")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(max = 5000, message = "公告内容长度不能超过5000位")
    private String content;

    @NotNull(message = "公告类型不能为空")
    @Min(value = 1, message = "公告类型不正确")
    @Max(value = 4, message = "公告类型不正确")
    private Integer type;

    @NotNull(message = "是否置顶不能为空")
    @Min(value = 0, message = "是否置顶取值不正确")
    @Max(value = 1, message = "是否置顶取值不正确")
    private Integer isTop;
}
