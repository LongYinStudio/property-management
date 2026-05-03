package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.EquipmentInspectionRequest;
import com.property.service.EquipmentInspectionService;
import com.property.vo.EquipmentInspectionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 设备巡检管理
 */
@RestController
@RequestMapping("/equipment-inspection")
@RequiredArgsConstructor
public class EquipmentInspectionController {

    private final EquipmentInspectionService equipmentInspectionService;

    /**
     * 新增设备巡检记录
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'OWNER')")
    public Result<EquipmentInspectionVO> create(@Valid @RequestBody EquipmentInspectionRequest request) {
        return Result.success("提交成功", equipmentInspectionService.create(request));
    }

    /**
     * 分页查询设备巡检记录
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'OWNER')")
    public Result<Page<EquipmentInspectionVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer equipmentType) {
        return Result.success(equipmentInspectionService.getPage(pageNum, pageSize, status, equipmentType));
    }

    /**
     * 获取设备巡检记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'OWNER')")
    public Result<EquipmentInspectionVO> getById(@PathVariable Long id) {
        return Result.success(equipmentInspectionService.getById(id));
    }

    /**
     * 删除设备巡检记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'OWNER')")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentInspectionService.delete(id);
        return Result.success("删除成功", null);
    }
}
