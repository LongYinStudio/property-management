package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.PropertyFeeRequest;
import com.property.vo.PropertyFeeVO;

/**
 * 物业费服务接口
 */
public interface PropertyFeeService {
    
    /**
     * 新增物业费（管理员/物业人员）
     */
    PropertyFeeVO create(PropertyFeeRequest request);
    
    /**
     * 分页查询物业费列表（业主查看自己的，管理员查看所有）
     */
    Page<PropertyFeeVO> getPage(Integer pageNum, Integer pageSize, Integer status, Integer type, Integer year);
    
    /**
     * 获取物业费详情
     */
    PropertyFeeVO getById(Long id);
    
    /**
     * 支付物业费
     */
    void pay(Long id);
    
    /**
     * 删除物业费（管理员/物业人员）
     */
    void delete(Long id);
}
