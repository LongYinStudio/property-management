package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.FacilityRequest;
import com.property.vo.FacilityVO;

import java.util.List;

/**
 * 设备设施服务接口
 */
public interface FacilityService {

    FacilityVO create(FacilityRequest request);

    FacilityVO update(Long id, FacilityRequest request);

    Page<FacilityVO> getPage(Integer pageNum, Integer pageSize, String keyword, Integer type, Integer status);

    FacilityVO getById(Long id);

    List<FacilityVO> getList(String keyword, Integer type, Integer status);

    void delete(Long id);
}
