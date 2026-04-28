package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.VisitorRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访客登记 Mapper
 */
@Mapper
public interface VisitorRecordMapper extends BaseMapper<VisitorRecord> {
}
