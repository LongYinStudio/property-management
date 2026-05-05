package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.BuildingRequest;
import com.property.vo.BuildingVO;

import java.util.List;

/**
 * 楼栋服务接口
 */
public interface BuildingService {

    BuildingVO create(BuildingRequest request);

    BuildingVO update(Long id, BuildingRequest request);

    Page<BuildingVO> getPage(Integer pageNum, Integer pageSize, Long communityId, String name);

    BuildingVO getById(Long id);

    List<BuildingVO> getList(Long communityId);

    void delete(Long id);
}
