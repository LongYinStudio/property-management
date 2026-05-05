package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.FacilityRequest;
import com.property.service.FacilityService;
import com.property.vo.FacilityVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备台账管理
 */
@RestController
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<FacilityVO> create(@Valid @RequestBody FacilityRequest request) {
        return Result.success("创建成功", facilityService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<FacilityVO> update(@PathVariable Long id, @Valid @RequestBody FacilityRequest request) {
        return Result.success("更新成功", facilityService.update(id, request));
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Page<FacilityVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(facilityService.getPage(pageNum, pageSize, keyword, type, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<FacilityVO> getById(@PathVariable Long id) {
        return Result.success(facilityService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'OWNER')")
    public Result<List<FacilityVO>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(facilityService.getList(keyword, type, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return Result.success("删除成功", null);
    }
}
