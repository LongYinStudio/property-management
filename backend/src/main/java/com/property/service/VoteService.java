package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.VoteActivityRequest;
import com.property.dto.VoteSubmitRequest;
import com.property.vo.VoteActivityVO;

/**
 * 投票问卷服务接口
 */
public interface VoteService {

    /**
     * 创建投票问卷活动
     */
    VoteActivityVO create(VoteActivityRequest request);

    /**
     * 分页查询活动
     */
    Page<VoteActivityVO> getPage(Integer pageNum, Integer pageSize, Integer type, Integer status, String title);

    /**
     * 获取活动详情
     */
    VoteActivityVO getById(Long id);

    /**
     * 提交投票/问卷
     */
    void submit(Long id, VoteSubmitRequest request);

    /**
     * 结束活动
     */
    void close(Long id);

    /**
     * 删除活动
     */
    void delete(Long id);
}
