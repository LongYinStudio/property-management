package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.UserRequest;
import com.property.service.UserService;
import com.property.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 新增用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> create(@Valid @RequestBody UserRequest request) {
        return Result.success("创建成功", userService.create(request));
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return Result.success("更新成功", userService.update(id, request));
    }
    
    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<UserVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer role) {
        return Result.success(userService.getPage(pageNum, pageSize, username, role));
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }
    
    /**
     * 获取业主列表
     */
    @GetMapping("/owners")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<List<UserVO>> getOwnerList() {
        return Result.success(userService.getOwnerList());
    }

    /**
     * 获取员工列表
     */
    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<List<UserVO>> getStaffList() {
        return Result.success(userService.getStaffList());
    }
}
