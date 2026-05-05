package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Room;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房屋Mapper
 */
@Mapper
public interface RoomMapper extends BaseMapper<Room> {
}
