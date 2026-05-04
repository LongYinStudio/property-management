package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.CommunityRequest;
import com.property.service.CommunityService;
import com.property.vo.CommunityVO;
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
 * 小区管理
 */
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 新增小区
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<CommunityVO> create(@Valid @RequestBody CommunityRequest request) {
        return Result.success("创建成功", communityService.create(request));
    }

    /**
     * 更新小区
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<CommunityVO> update(@PathVariable Long id, @Valid @RequestBody CommunityRequest request) {
        return Result.success("更新成功", communityService.update(id, request));
    }

    /**
     * 分页查询小区
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Page<CommunityVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        return Result.success(communityService.getPage(pageNum, pageSize, name));
    }

    /**
     * 获取小区详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<CommunityVO> getById(@PathVariable Long id) {
        return Result.success(communityService.getById(id));
    }

    /**
     * 获取小区列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<List<CommunityVO>> getList() {
        return Result.success(communityService.getList());
    }

    /**
     * 删除小区
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        communityService.delete(id);
        return Result.success("删除成功", null);
    }
}
