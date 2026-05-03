package com.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 指派维修人员请求DTO
 */
@Data
public class RepairAssignRequest {

    @NotNull(message = "维修人员不能为空")
    @Positive(message = "维修人员ID必须大于0")
    private Long handlerId;
}
