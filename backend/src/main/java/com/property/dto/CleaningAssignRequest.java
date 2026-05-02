package com.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 指派清洁人员请求DTO
 */
@Data
public class CleaningAssignRequest {

    @NotNull(message = "清洁人员不能为空")
    private Long cleanerId;
}
