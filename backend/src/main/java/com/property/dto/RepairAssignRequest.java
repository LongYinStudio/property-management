package com.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 指派维修人员请求DTO
 */
@Data
public class RepairAssignRequest {

    @NotNull(message = "维修人员不能为空")
    private Long handlerId;
}
