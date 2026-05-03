package com.property.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访客登记请求 DTO
 */
@Data
public class VisitorRecordRequest {

    @NotBlank(message = "访客姓名不能为空")
    @Size(max = 50, message = "访客姓名长度不能超过50位")
    private String visitorName;

    @NotBlank(message = "访客手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "访客手机号格式不正确")
    private String visitorPhone;

    @NotNull(message = "来访人数不能为空")
    @Min(value = 1, message = "来访人数至少为1")
    private Integer visitorCount;

    @Size(max = 20, message = "车牌号长度不能超过20位")
    private String licensePlate;

    @NotBlank(message = "来访事由不能为空")
    @Size(max = 255, message = "来访事由长度不能超过255位")
    private String purpose;

    @NotNull(message = "到访时间不能为空")
    private LocalDateTime visitTime;

    private LocalDateTime validUntil;

    @Size(max = 255, message = "备注长度不能超过255位")
    private String remark;

    @AssertTrue(message = "有效截止时间不能早于到访时间")
    public boolean isValidUntilAfterVisitTime() {
        return validUntil == null || visitTime == null || !validUntil.isBefore(visitTime);
    }
}
