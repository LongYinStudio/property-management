package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投诉建议Mapper
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
}
