package com.property.vo;

import lombok.Data;

/**
 * 访客登记 VO
 */
@Data
public class VisitorRecordVO {

    private Long id;

    private Long ownerId;

    private String ownerName;

    private String visitorName;

    private String visitorPhone;

    private Integer visitorCount;

    private String licensePlate;

    private String purpose;

    private String visitTime;

    private String validUntil;

    private String passCode;

    private Integer status;

    private Long verifierId;

    private String verifierName;

    private String verifyTime;

    private String remark;

    private String createTime;
}
