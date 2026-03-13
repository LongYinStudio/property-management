package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.CleaningRequest;
import com.property.service.CleaningService;
import com.property.vo.CleaningVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 清洁管理
 */
@RestController
@RequestMapping("/cleaning")
@RequiredArgsConstructor
public class CleaningController {
    
    private final CleaningService cleaningService;
    
    /**
     * 新增清洁任务
     */
    @PostMapping
    public Result<CleaningVO> create(@Valid @RequestBody CleaningRequest request) {
        return Result.success("提交成功", cleaningService.create(request));
    }
    
    /**
     * 分页查询清洁任务列表
     */
    @GetMapping("/page")
    public Result<Page<CleaningVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(cleaningService.getPage(pageNum, pageSize, status));
    }
    
    /**
     * 获取清洁任务详情
     */
    @GetMapping("/{id}")
    public Result<CleaningVO> getById(@PathVariable Long id) {
        return Result.success(cleaningService.getById(id));
    }
    
    /**
     * 删除清洁任务
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cleaningService.delete(id);
        return Result.success("删除成功", null);
    }
}
