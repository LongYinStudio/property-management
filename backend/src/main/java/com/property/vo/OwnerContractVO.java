package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 业主合同返回VO
 */
@Data
public class OwnerContractVO {

    private Long id;

    private Long userId;

    private String userName;

    private String userPhone;

    private String contractNo;

    private String contractName;

    private Integer contractType;

    private String roomInfo;

    private String signDate;

    private String startDate;

    private String endDate;

    private BigDecimal amount;

    private Integer status;

    private String attachmentName;

    private String attachmentUrl;

    private String remark;

    private Long creatorId;

    private String creatorName;

    private String createTime;
}
