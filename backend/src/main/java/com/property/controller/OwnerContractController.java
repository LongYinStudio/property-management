package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.OwnerContractRequest;
import com.property.service.OwnerContractService;
import com.property.vo.OwnerContractVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 业主合同管理
 */
@RestController
@RequestMapping("/owner-contract")
@RequiredArgsConstructor
public class OwnerContractController {

    private final OwnerContractService ownerContractService;

    /**
     * 新增合同
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<OwnerContractVO> create(@Valid @RequestBody OwnerContractRequest request) {
        return Result.success("创建成功", ownerContractService.create(request));
    }

    /**
     * 更新合同
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<OwnerContractVO> update(@PathVariable Long id, @Valid @RequestBody OwnerContractRequest request) {
        return Result.success("更新成功", ownerContractService.update(id, request));
    }

    /**
     * 分页查询合同
     */
    @GetMapping("/page")
    public Result<Page<OwnerContractVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer contractType,
            @RequestParam(required = false) Long userId
    ) {
        return Result.success(ownerContractService.getPage(pageNum, pageSize, keyword, status, contractType, userId));
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    public Result<OwnerContractVO> getById(@PathVariable Long id) {
        return Result.success(ownerContractService.getById(id));
    }

    /**
     * 删除合同
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        ownerContractService.delete(id);
        return Result.success("删除成功", null);
    }
}
