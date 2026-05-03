package com.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.dto.NoticeRequest;
import com.property.vo.NoticeVO;

/**
 * 通知公告服务接口
 */
public interface NoticeService {

    /**
     * 新增公告
     */
    NoticeVO create(NoticeRequest request);

    /**
     * 更新公告
     */
    NoticeVO update(Long id, NoticeRequest request);

    /**
     * 分页查询公告
     */
    Page<NoticeVO> getPage(Integer pageNum, Integer pageSize, String title, Integer type, Integer status);

    /**
     * 获取公告详情
     */
    NoticeVO getById(Long id);

    /**
     * 发布公告
     */
    NoticeVO publish(Long id);

    /**
     * 撤回公告
     */
    NoticeVO revoke(Long id);

    /**
     * 删除公告
     */
    void delete(Long id);
}
