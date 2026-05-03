package com.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 指派清洁人员请求DTO
 */
@Data
public class CleaningAssignRequest {

    @NotNull(message = "清洁人员不能为空")
    @Positive(message = "清洁人员ID必须大于0")
    private Long cleanerId;
}
