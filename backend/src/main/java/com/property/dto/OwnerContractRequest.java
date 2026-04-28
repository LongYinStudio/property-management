package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 业主合同请求DTO
 */
@Data
public class OwnerContractRequest {

    @NotNull(message = "业主不能为空")
    private Long userId;

    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @NotNull(message = "合同类型不能为空")
    private Integer contractType;

    private String roomInfo;

    @NotNull(message = "签署日期不能为空")
    private LocalDate signDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal amount;

    @NotNull(message = "合同状态不能为空")
    private Integer status;

    private String attachmentName;

    private String attachmentUrl;

    private String remark;
}
