package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.BusinessException;
import com.property.dto.NoticeRequest;
import com.property.entity.Notice;
import com.property.entity.User;
import com.property.mapper.NoticeMapper;
import com.property.mapper.UserMapper;
import com.property.security.LoginUser;
import com.property.service.NoticeService;
import com.property.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通知公告服务实现
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final NoticeMapper noticeMapper;
    private final UserMapper userMapper;

    @Override
    public NoticeVO create(NoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setType(request.getType());
        notice.setIsTop(normalizeTopValue(request.getIsTop()));
        notice.setStatus(Notice.STATUS_DRAFT);
        notice.setPublisherId(getCurrentUser().getId());
        notice.setDeleted(0);
        noticeMapper.insert(notice);
        return convertToVO(notice);
    }

    @Override
    public NoticeVO update(Long id, NoticeRequest request) {
        Notice notice = getNoticeOrThrow(id);
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setType(request.getType());
        notice.setIsTop(normalizeTopValue(request.getIsTop()));
        noticeMapper.updateById(notice);
        return convertToVO(notice);
    }

    @Override
    public Page<NoticeVO> getPage(Integer pageNum, Integer pageSize, String title, Integer type, Integer status) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(title)) {
            queryWrapper.like(Notice::getTitle, title.trim());
        }
        if (type != null) {
            queryWrapper.eq(Notice::getType, type);
        }

        if (isOwner()) {
            queryWrapper.eq(Notice::getStatus, Notice.STATUS_PUBLISHED);
        } else if (status != null) {
            queryWrapper.eq(Notice::getStatus, status);
        }

        queryWrapper.orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getPublishTime)
                .orderByDesc(Notice::getCreateTime);

        Page<Notice> noticePage = noticeMapper.selectPage(page, queryWrapper);
        Page<NoticeVO> voPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        voPage.setRecords(noticePage.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public NoticeVO getById(Long id) {
        Notice notice = getNoticeOrThrow(id);
        if (isOwner() && (notice.getStatus() == null || Notice.STATUS_PUBLISHED != notice.getStatus())) {
            throw new BusinessException("该公告暂不可查看");
        }
        return convertToVO(notice);
    }

    @Override
    public NoticeVO publish(Long id) {
        Notice notice = getNoticeOrThrow(id);
        if (Notice.STATUS_PUBLISHED == notice.getStatus()) {
            throw new BusinessException("该公告已发布");
        }
        notice.setStatus(Notice.STATUS_PUBLISHED);
        notice.setPublishTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        return convertToVO(notice);
    }

    @Override
    public NoticeVO revoke(Long id) {
        Notice notice = getNoticeOrThrow(id);
        if (Notice.STATUS_DRAFT == notice.getStatus()) {
            throw new BusinessException("草稿状态无需撤回");
        }
        if (Notice.STATUS_REVOKED == notice.getStatus()) {
            throw new BusinessException("该公告已撤回");
        }
        notice.setStatus(Notice.STATUS_REVOKED);
        noticeMapper.updateById(notice);
        return convertToVO(notice);
    }

    @Override
    public void delete(Long id) {
        Notice notice = getNoticeOrThrow(id);
        noticeMapper.deleteById(notice.getId());
    }

    private Notice getNoticeOrThrow(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    private NoticeVO convertToVO(Notice notice) {
        NoticeVO vo = new NoticeVO();
        BeanUtils.copyProperties(notice, vo);

        User publisher = userMapper.selectById(notice.getPublisherId());
        if (publisher != null) {
            vo.setPublisherName(StringUtils.hasText(publisher.getRealName()) ? publisher.getRealName() : publisher.getUsername());
        }

        if (notice.getPublishTime() != null) {
            vo.setPublishTime(notice.getPublishTime().format(FORMATTER));
        }
        if (notice.getCreateTime() != null) {
            vo.setCreateTime(notice.getCreateTime().format(FORMATTER));
        }
        if (notice.getUpdateTime() != null) {
            vo.setUpdateTime(notice.getUpdateTime().format(FORMATTER));
        }
        return vo;
    }

    private Integer normalizeTopValue(Integer isTop) {
        return Integer.valueOf(1).equals(isTop) ? 1 : 0;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser.getUser();
    }

    private boolean isOwner() {
        return Integer.valueOf(User.ROLE_OWNER).equals(getCurrentUser().getRole());
    }
}
