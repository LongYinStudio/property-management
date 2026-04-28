package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.VoteActivityRequest;
import com.property.dto.VoteSubmitRequest;
import com.property.service.VoteService;
import com.property.vo.VoteActivityVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 投票问卷管理
 */
@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    /**
     * 创建活动
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<VoteActivityVO> create(@Valid @RequestBody VoteActivityRequest request) {
        return Result.success("创建成功", voteService.create(request));
    }

    /**
     * 分页查询活动
     */
    @GetMapping("/page")
    public Result<Page<VoteActivityVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String title) {
        return Result.success(voteService.getPage(pageNum, pageSize, type, status, title));
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<VoteActivityVO> getById(@PathVariable Long id) {
        return Result.success(voteService.getById(id));
    }

    /**
     * 参与活动
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('OWNER')")
    public Result<Void> submit(@PathVariable Long id, @Valid @RequestBody VoteSubmitRequest request) {
        voteService.submit(id, request);
        return Result.success("提交成功", null);
    }

    /**
     * 结束活动
     */
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> close(@PathVariable Long id) {
        voteService.close(id);
        return Result.success("活动已结束", null);
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        voteService.delete(id);
        return Result.success("删除成功", null);
    }
}
