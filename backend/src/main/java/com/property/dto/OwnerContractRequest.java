package com.property.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 业主合同请求DTO
 */
@Data
public class OwnerContractRequest {

    @NotNull(message = "业主不能为空")
    @Positive(message = "业主ID必须大于0")
    private Long userId;

    @NotBlank(message = "合同编号不能为空")
    @Size(max = 64, message = "合同编号长度不能超过64位")
    private String contractNo;

    @NotBlank(message = "合同名称不能为空")
    @Size(max = 100, message = "合同名称长度不能超过100位")
    private String contractName;

    @NotNull(message = "合同类型不能为空")
    @Min(value = 1, message = "合同类型不正确")
    @Max(value = 5, message = "合同类型不正确")
    private Integer contractType;

    @Size(max = 100, message = "房屋信息长度不能超过100位")
    private String roomInfo;

    @NotNull(message = "签署日期不能为空")
    private LocalDate signDate;

    private LocalDate startDate;

    private LocalDate endDate;

    @DecimalMin(value = "0.00", message = "合同金额不能小于0")
    private BigDecimal amount;

    @NotNull(message = "合同状态不能为空")
    @Min(value = 1, message = "合同状态不正确")
    @Max(value = 4, message = "合同状态不正确")
    private Integer status;

    @Size(max = 255, message = "附件名称长度不能超过255位")
    private String attachmentName;

    @Size(max = 500, message = "附件地址长度不能超过500位")
    private String attachmentUrl;

    @Size(max = 500, message = "备注长度不能超过500位")
    private String remark;

    @AssertTrue(message = "结束日期不能早于开始日期")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
