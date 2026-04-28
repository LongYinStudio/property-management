package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.VisitorRecordRequest;
import com.property.vo.VisitorRecordVO;

/**
 * 访客服务接口
 */
public interface VisitorService {

    /**
     * 创建访客登记
     */
    VisitorRecordVO create(VisitorRecordRequest request);

    /**
     * 分页查询访客记录
     */
    Page<VisitorRecordVO> getPage(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /**
     * 获取访客详情
     */
    VisitorRecordVO getById(Long id);

    /**
     * 核销通行证
     */
    void verify(Long id);

    /**
     * 取消登记
     */
    void cancel(Long id);
}
