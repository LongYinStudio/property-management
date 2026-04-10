package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.ParkingRentalRequest;
import com.property.dto.ParkingSpaceRequest;
import com.property.service.ParkingSpaceService;
import com.property.vo.ParkingRentalVO;
import com.property.vo.ParkingSpaceVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 车位管理
 */
@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingSpaceController {
    
    private final ParkingSpaceService parkingSpaceService;
    
    /**
     * 新增车位（管理员/物业人员）
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<ParkingSpaceVO> createSpace(@Valid @RequestBody ParkingSpaceRequest request) {
        return Result.success("创建成功", parkingSpaceService.createSpace(request));
    }
    
    /**
     * 更新车位（管理员/物业人员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<ParkingSpaceVO> updateSpace(@PathVariable Long id, @Valid @RequestBody ParkingSpaceRequest request) {
        return Result.success("更新成功", parkingSpaceService.updateSpace(id, request));
    }
    
    /**
     * 分页查询车位列表
     */
    @GetMapping("/page")
    public Result<Page<ParkingSpaceVO>> getSpacePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {
        return Result.success(parkingSpaceService.getSpacePage(pageNum, pageSize, status, type));
    }
    
    /**
     * 获取车位详情
     */
    @GetMapping("/{id}")
    public Result<ParkingSpaceVO> getSpaceById(@PathVariable Long id) {
        return Result.success(parkingSpaceService.getSpaceById(id));
    }
    
    /**
     * 删除车位（管理员/物业人员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> deleteSpace(@PathVariable Long id) {
        parkingSpaceService.deleteSpace(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 分配车位（出售）（管理员/物业人员）
     */
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<ParkingSpaceVO> assignSpace(@PathVariable Long id, @RequestParam Long userId) {
        return Result.success("分配成功", parkingSpaceService.assignSpace(id, userId));
    }
    
    /**
     * 租用车位
     */
    @PostMapping("/rent")
    public Result<ParkingRentalVO> rentSpace(@Valid @RequestBody ParkingRentalRequest request) {
        return Result.success("租用成功", parkingSpaceService.rentSpace(request));
    }
    
    /**
     * 续费车位
     */
    @PostMapping("/rent/{rentalId}/renew")
    public Result<ParkingRentalVO> renewSpace(@PathVariable Long rentalId, @Valid @RequestBody ParkingRentalRequest request) {
        return Result.success("续费成功", parkingSpaceService.renewSpace(rentalId, request));
    }
    
    /**
     * 分页查询租用记录
     */
    @GetMapping("/rent/page")
    public Result<Page<ParkingRentalVO>> getRentalPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long spaceId,
            @RequestParam(required = false) Integer status) {
        return Result.success(parkingSpaceService.getRentalPage(pageNum, pageSize, spaceId, status));
    }
    
    /**
     * 支付租用费用
     */
    @PostMapping("/rent/{id}/pay")
    public Result<Void> payRental(@PathVariable Long id) {
        parkingSpaceService.payRental(id);
        return Result.success("支付成功", null);
    }
    
    /**
     * 取消租用
     */
    @DeleteMapping("/rent/{id}")
    public Result<Void> cancelRental(@PathVariable Long id) {
        parkingSpaceService.cancelRental(id);
        return Result.success("取消成功", null);
    }
}
