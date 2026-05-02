package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.RepairAssignRequest;
import com.property.dto.RepairCompleteRequest;
import com.property.dto.RepairRequest;
import com.property.service.RepairService;
import com.property.vo.RepairVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 报修管理
 */
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class RepairController {
    
    private final RepairService repairService;
    
    /**
     * 新增报修
     */
    @PostMapping
    public Result<RepairVO> create(@Valid @RequestBody RepairRequest request) {
        return Result.success("提交成功", repairService.create(request));
    }
    
    /**
     * 分页查询报修列表
     */
    @GetMapping("/page")
    public Result<Page<RepairVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(repairService.getPage(pageNum, pageSize, status));
    }
    
    /**
     * 获取报修详情
     */
    @GetMapping("/{id}")
    public Result<RepairVO> getById(@PathVariable Long id) {
        return Result.success(repairService.getById(id));
    }
    
    /**
     * 删除报修
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        repairService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 指派维修人员
     */
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<RepairVO> assign(@PathVariable Long id, @Valid @RequestBody RepairAssignRequest request) {
        return Result.success("指派成功", repairService.assign(id, request.getHandlerId()));
    }

    /**
     * 完成报修处理
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<RepairVO> complete(@PathVariable Long id, @Valid @RequestBody RepairCompleteRequest request) {
        return Result.success("已完成", repairService.complete(id, request.getHandleResult()));
    }
}
