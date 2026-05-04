package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.CommunityRequest;
import com.property.vo.CommunityVO;

import java.util.List;

/**
 * 小区服务接口
 */
public interface CommunityService {

    /**
     * 新增小区
     */
    CommunityVO create(CommunityRequest request);

    /**
     * 更新小区
     */
    CommunityVO update(Long id, CommunityRequest request);

    /**
     * 分页查询小区
     */
    Page<CommunityVO> getPage(Integer pageNum, Integer pageSize, String name);

    /**
     * 获取小区详情
     */
    CommunityVO getById(Long id);

    /**
     * 获取小区列表
     */
    List<CommunityVO> getList();

    /**
     * 删除小区
     */
    void delete(Long id);
}
