package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Building;
import org.apache.ibatis.annotations.Mapper;

/**
 * 楼栋Mapper
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {
}
