package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.ComplaintRequest;
import com.property.vo.ComplaintVO;

/**
 * 投诉建议服务接口
 */
public interface ComplaintService {
    
    /**
     * 新增投诉/建议
     */
    ComplaintVO create(ComplaintRequest request);
    
    /**
     * 分页查询投诉/建议列表
     */
    Page<ComplaintVO> getPage(Integer pageNum, Integer pageSize, Integer type);
    
    /**
     * 获取投诉/建议详情
     */
    ComplaintVO getById(Long id);
    
    /**
     * 删除投诉/建议
     */
    void delete(Long id);
}
