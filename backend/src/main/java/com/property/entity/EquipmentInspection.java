package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备巡检记录实体类
 */
@Data
@TableName("equipment_inspection")
public class EquipmentInspection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String equipmentName;

    private Integer equipmentType;

    private String location;

    private LocalDate inspectionDate;

    private LocalDate nextInspectionDate;

    private Integer status;

    private String result;

    private String issueDescription;

    private String images;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /**
     * 巡检状态
     */
    public static final int STATUS_NORMAL = 0;      // 正常
    public static final int STATUS_ABNORMAL = 1;    // 异常
    public static final int STATUS_REPAIRED = 2;    // 已维修
}
