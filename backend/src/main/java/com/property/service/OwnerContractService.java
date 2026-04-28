package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.OwnerContractRequest;
import com.property.vo.OwnerContractVO;

/**
 * 业主合同服务接口
 */
public interface OwnerContractService {

    /**
     * 新增业主合同
     */
    OwnerContractVO create(OwnerContractRequest request);

    /**
     * 更新业主合同
     */
    OwnerContractVO update(Long id, OwnerContractRequest request);

    /**
     * 分页查询业主合同
     */
    Page<OwnerContractVO> getPage(
            Integer pageNum,
            Integer pageSize,
            String keyword,
            Integer status,
            Integer contractType,
            Long userId
    );

    /**
     * 获取合同详情
     */
    OwnerContractVO getById(Long id);

    /**
     * 删除合同
     */
    void delete(Long id);
}
