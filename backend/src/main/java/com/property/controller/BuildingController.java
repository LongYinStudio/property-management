package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.BuildingRequest;
import com.property.service.BuildingService;
import com.property.vo.BuildingVO;
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
 * 楼栋管理
 */
@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<BuildingVO> create(@Valid @RequestBody BuildingRequest request) {
        return Result.success("创建成功", buildingService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<BuildingVO> update(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
        return Result.success("更新成功", buildingService.update(id, request));
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Page<BuildingVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) String name) {
        return Result.success(buildingService.getPage(pageNum, pageSize, communityId, name));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<BuildingVO> getById(@PathVariable Long id) {
        return Result.success(buildingService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<List<BuildingVO>> getList(@RequestParam(required = false) Long communityId) {
        return Result.success(buildingService.getList(communityId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.delete(id);
        return Result.success("删除成功", null);
    }
}
