package com.property.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 楼栋请求DTO
 */
@Data
public class BuildingRequest {

    @NotNull(message = "小区不能为空")
    @Positive(message = "小区ID必须大于0")
    private Long communityId;

    @NotBlank(message = "楼栋名称不能为空")
    @Size(max = 50, message = "楼栋名称长度不能超过50位")
    private String name;

    @Min(value = 1, message = "楼层数不能小于1")
    private Integer floors;

    @Min(value = 1, message = "单元数不能小于1")
    private Integer units;

    @Min(value = 1, message = "每层房间数不能小于1")
    private Integer roomsPerFloor;
}
