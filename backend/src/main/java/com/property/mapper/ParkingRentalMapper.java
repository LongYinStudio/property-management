package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.ParkingRental;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车位租用记录Mapper
 */
@Mapper
public interface ParkingRentalMapper extends BaseMapper<ParkingRental> {
}
