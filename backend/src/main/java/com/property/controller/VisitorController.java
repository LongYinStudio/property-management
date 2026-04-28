package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.VisitorRecordRequest;
import com.property.service.VisitorService;
import com.property.vo.VisitorRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 访客管理
 */
@RestController
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    /**
     * 新增访客登记
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public Result<VisitorRecordVO> create(@Valid @RequestBody VisitorRecordRequest request) {
        return Result.success("登记成功", visitorService.create(request));
    }

    /**
     * 分页查询访客记录
     */
    @GetMapping("/page")
    public Result<Page<VisitorRecordVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(visitorService.getPage(pageNum, pageSize, keyword, status));
    }

    /**
     * 获取详情
     */
    @GetMapping("/{id}")
    public Result<VisitorRecordVO> getById(@PathVariable Long id) {
        return Result.success(visitorService.getById(id));
    }

    /**
     * 核销通行证
     */
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> verify(@PathVariable Long id) {
        visitorService.verify(id);
        return Result.success("核销成功", null);
    }

    /**
     * 取消登记
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        visitorService.cancel(id);
        return Result.success("已取消", null);
    }
}
