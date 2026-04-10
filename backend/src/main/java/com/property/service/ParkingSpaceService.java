package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.ParkingRentalRequest;
import com.property.dto.ParkingSpaceRequest;
import com.property.vo.ParkingRentalVO;
import com.property.vo.ParkingSpaceVO;

/**
 * 车位服务接口
 */
public interface ParkingSpaceService {
    
    /**
     * 新增车位
     */
    ParkingSpaceVO createSpace(ParkingSpaceRequest request);
    
    /**
     * 更新车位
     */
    ParkingSpaceVO updateSpace(Long id, ParkingSpaceRequest request);
    
    /**
     * 分页查询车位列表
     */
    Page<ParkingSpaceVO> getSpacePage(Integer pageNum, Integer pageSize, Integer status, Integer type);
    
    /**
     * 获取车位详情
     */
    ParkingSpaceVO getSpaceById(Long id);
    
    /**
     * 删除车位
     */
    void deleteSpace(Long id);
    
    /**
     * 分配车位（出售）
     */
    ParkingSpaceVO assignSpace(Long id, Long userId);
    
    /**
     * 租用车位
     */
    ParkingRentalVO rentSpace(ParkingRentalRequest request);
    
    /**
     * 续费车位
     */
    ParkingRentalVO renewSpace(Long rentalId, ParkingRentalRequest request);
    
    /**
     * 分页查询租用记录
     */
    Page<ParkingRentalVO> getRentalPage(Integer pageNum, Integer pageSize, Long spaceId, Integer status);
    
    /**
     * 支付租用费用
     */
    void payRental(Long id);
    
    /**
     * 取消租用
     */
    void cancelRental(Long id);
}
