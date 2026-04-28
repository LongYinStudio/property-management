package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.VoteResponse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投票问卷参与记录 Mapper
 */
@Mapper
public interface VoteResponseMapper extends BaseMapper<VoteResponse> {
}
