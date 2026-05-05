package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.RoomRequest;
import com.property.vo.RoomVO;

import java.util.List;

/**
 * 房屋服务接口
 */
public interface RoomService {

    RoomVO create(RoomRequest request);

    RoomVO update(Long id, RoomRequest request);

    Page<RoomVO> getPage(Integer pageNum, Integer pageSize, Long communityId, Long buildingId, String roomNumber, Integer status);

    RoomVO getById(Long id);

    List<RoomVO> getList(Long communityId, Long buildingId);

    void delete(Long id);
}
