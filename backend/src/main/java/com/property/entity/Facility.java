package com.property.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备设施实体类
 */
@Data
@TableName("facility")
public class Facility implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer type;

    private String location;

    private Integer status;

    private LocalDate lastCheckDate;

    private LocalDate nextCheckDate;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public static final int TYPE_FIRE = 1;
    public static final int TYPE_ELEVATOR = 2;
    public static final int TYPE_MONITOR = 3;
    public static final int TYPE_OTHER = 4;

    public static final int STATUS_FAULT = 0;
    public static final int STATUS_NORMAL = 1;
}
