package com.property.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访客登记请求 DTO
 */
@Data
public class VisitorRecordRequest {

    @NotBlank(message = "访客姓名不能为空")
    private String visitorName;

    @NotBlank(message = "访客手机号不能为空")
    private String visitorPhone;

    @NotNull(message = "来访人数不能为空")
    @Min(value = 1, message = "来访人数至少为1")
    private Integer visitorCount;

    private String licensePlate;

    @NotBlank(message = "来访事由不能为空")
    private String purpose;

    @NotNull(message = "到访时间不能为空")
    private LocalDateTime visitTime;

    private LocalDateTime validUntil;

    private String remark;
}
