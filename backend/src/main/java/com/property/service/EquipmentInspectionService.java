package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.EquipmentInspectionRequest;
import com.property.vo.EquipmentInspectionVO;

/**
 * 设备巡检服务接口
 */
public interface EquipmentInspectionService {

    /**
     * 新增设备巡检记录
     */
    EquipmentInspectionVO create(EquipmentInspectionRequest request);

    /**
     * 分页查询设备巡检记录
     */
    Page<EquipmentInspectionVO> getPage(Integer pageNum, Integer pageSize, Integer status, Integer equipmentType);

    /**
     * 获取设备巡检记录详情
     */
    EquipmentInspectionVO getById(Long id);

    /**
     * 删除设备巡检记录
     */
    void delete(Long id);
}
