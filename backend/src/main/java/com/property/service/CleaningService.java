package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.CleaningRequest;
import com.property.vo.CleaningVO;

/**
 * 清洁服务接口
 */
public interface CleaningService {
    
    /**
     * 新增清洁任务
     */
    CleaningVO create(CleaningRequest request);
    
    /**
     * 分页查询清洁任务列表
     */
    Page<CleaningVO> getPage(Integer pageNum, Integer pageSize, Integer status);
    
    /**
     * 获取清洁任务详情
     */
    CleaningVO getById(Long id);
    
    /**
     * 删除清洁任务
     */
    void delete(Long id);
}
