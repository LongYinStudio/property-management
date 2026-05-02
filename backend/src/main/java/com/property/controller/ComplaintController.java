package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.ComplaintReplyRequest;
import com.property.dto.ComplaintRequest;
import com.property.service.ComplaintService;
import com.property.vo.ComplaintVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 投诉建议管理
 */
@RestController
@RequestMapping("/complaint")
@RequiredArgsConstructor
public class ComplaintController {
    
    private final ComplaintService complaintService;
    
    /**
     * 新增投诉/建议
     */
    @PostMapping
    public Result<ComplaintVO> create(@Valid @RequestBody ComplaintRequest request) {
        return Result.success("提交成功", complaintService.create(request));
    }
    
    /**
     * 分页查询投诉/建议列表
     */
    @GetMapping("/page")
    public Result<Page<ComplaintVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type) {
        return Result.success(complaintService.getPage(pageNum, pageSize, type));
    }
    
    /**
     * 获取投诉/建议详情
     */
    @GetMapping("/{id}")
    public Result<ComplaintVO> getById(@PathVariable Long id) {
        return Result.success(complaintService.getById(id));
    }
    
    /**
     * 删除投诉/建议
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        complaintService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 回复投诉/建议
     */
    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<ComplaintVO> reply(@PathVariable Long id, @Valid @RequestBody ComplaintReplyRequest request) {
        return Result.success("回复成功", complaintService.reply(id, request.getReply()));
    }

    /**
     * 关闭投诉/建议
     */
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<ComplaintVO> close(@PathVariable Long id) {
        return Result.success("已关闭", complaintService.close(id));
    }
}
