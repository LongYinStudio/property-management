package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.NoticeRequest;
import com.property.service.NoticeService;
import com.property.vo.NoticeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 通知公告管理
 */
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 新增公告
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<NoticeVO> create(@Valid @RequestBody NoticeRequest request) {
        return Result.success("保存成功", noticeService.create(request));
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<NoticeVO> update(@PathVariable Long id, @Valid @RequestBody NoticeRequest request) {
        return Result.success("更新成功", noticeService.update(id, request));
    }

    /**
     * 分页查询公告
     */
    @GetMapping("/page")
    public Result<Page<NoticeVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(noticeService.getPage(pageNum, pageSize, title, type, status));
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<NoticeVO> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    /**
     * 发布公告
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<NoticeVO> publish(@PathVariable Long id) {
        return Result.success("发布成功", noticeService.publish(id));
    }

    /**
     * 撤回公告
     */
    @PostMapping("/{id}/revoke")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<NoticeVO> revoke(@PathVariable Long id) {
        return Result.success("撤回成功", noticeService.revoke(id));
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success("删除成功", null);
    }
}
