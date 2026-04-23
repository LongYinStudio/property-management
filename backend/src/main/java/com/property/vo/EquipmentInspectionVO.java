package com.property.vo;

import lombok.Data;

/**
 * 设备巡检记录返回VO
 */
@Data
public class EquipmentInspectionVO {

    private Long id;

    private Long userId;

    private String userName;

    private String equipmentName;

    private Integer equipmentType;

    private String location;

    private String inspectionDate;

    private String nextInspectionDate;

    private Integer status;

    private String result;

    private String issueDescription;

    private String images;

    private String remark;

    private String createTime;
}
