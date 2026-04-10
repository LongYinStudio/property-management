package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.ParkingSpace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车位Mapper
 */
@Mapper
public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {
}
