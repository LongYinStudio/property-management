package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.RepairRequest;
import com.property.vo.RepairVO;

/**
 * 报修服务接口
 */
public interface RepairService {
    
    /**
     * 新增报修
     */
    RepairVO create(RepairRequest request);
    
    /**
     * 分页查询报修列表
     */
    Page<RepairVO> getPage(Integer pageNum, Integer pageSize, Integer status);
    
    /**
     * 获取报修详情
     */
    RepairVO getById(Long id);
    
    /**
     * 删除报修
     */
    void delete(Long id);
}
