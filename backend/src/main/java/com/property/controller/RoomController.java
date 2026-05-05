package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.RoomRequest;
import com.property.service.RoomService;
import com.property.vo.RoomVO;
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
 * 房屋管理
 */
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<RoomVO> create(@Valid @RequestBody RoomRequest request) {
        return Result.success("创建成功", roomService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<RoomVO> update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return Result.success("更新成功", roomService.update(id, request));
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Page<RoomVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer status) {
        return Result.success(roomService.getPage(pageNum, pageSize, communityId, buildingId, roomNumber, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<RoomVO> getById(@PathVariable Long id) {
        return Result.success(roomService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<List<RoomVO>> getList(
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) Long buildingId) {
        return Result.success(roomService.getList(communityId, buildingId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.success("删除成功", null);
    }
}
